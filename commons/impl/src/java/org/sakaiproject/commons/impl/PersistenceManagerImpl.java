/**
 * Copyright (c) 2003-2017 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.commons.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.commons.api.datamodel.Comment;
import org.sakaiproject.commons.api.datamodel.Commons;
import org.sakaiproject.commons.api.datamodel.Post;
import org.sakaiproject.commons.api.datamodel.PostLike;
import org.sakaiproject.commons.api.CommonsConstants;
import org.sakaiproject.commons.api.PersistenceManager;
import org.sakaiproject.commons.api.QueryBean;
import org.sakaiproject.commons.api.SakaiProxy;
import org.sakaiproject.user.api.User;

/**
 * @author Adrian Fish (adrian.r.fish@gmail.com)
 */
@Getter @Setter @Slf4j
public class PersistenceManagerImpl implements PersistenceManager {

    private static final String POST_SELECT
        = "SELECT cp.*,cw.ID as COMMONS_ID,cw.SITE_ID,cw.EMBEDDER FROM COMMONS_POST cp, COMMONS_COMMONS cw, COMMONS_COMMONS_POST cwp "
            + "WHERE cp.ID = ? AND cp.ID = cwp.POST_ID and cwp.COMMONS_ID = cw.ID";
    private static final String COMMONS_SELECT = "SELECT * FROM COMMONS_COMMONS WHERE ID = ?";
    private static final String COMMONS_POSTS_SELECT
        = "SELECT cw.ID as COMMONS_ID,cw.SITE_ID,cw.EMBEDDER,cp.* FROM COMMONS_COMMONS cw,COMMONS_COMMONS_POST cwp,COMMONS_POST cp "
            + "WHERE cw.ID = ? AND cwp.COMMONS_ID = cw.ID AND cp.ID = cwp.POST_ID ORDER BY CREATED_DATE DESC";
    private static final String SOCIAL_COMMONS_POSTS_SELECT
        = "SELECT cw.ID as COMMONS_ID,cw.SITE_ID,cw.EMBEDDER,cp.* FROM COMMONS_COMMONS cw,COMMONS_COMMONS_POST cwp,COMMONS_POST cp "
            + "WHERE cw.ID = ? AND cwp.COMMONS_ID = cw.ID AND cp.ID = cwp.POST_ID AND CREATOR_ID IN (";
    private static final String COMMONS_POST_INSERT = "INSERT INTO COMMONS_COMMONS_POST VALUES(?,?)";
    private static final String COMMONS_INSERT = "INSERT INTO COMMONS_COMMONS VALUES(?,?,?)";
    private static final String COMMENT_SELECT = "SELECT * FROM COMMONS_COMMENT WHERE ID = ?";
    private static final String COMMENTS_SELECT = "SELECT * FROM COMMONS_COMMENT WHERE POST_ID = ? ORDER BY CREATED_DATE ASC";
    private static final String COMMENT_INSERT = "INSERT INTO COMMONS_COMMENT VALUES(?,?,?,?,?,?)";
    private static final String COMMENT_UPDATE = "UPDATE COMMONS_COMMENT SET CONTENT = ?, MODIFIED_DATE = ? WHERE ID = ?";
    private static final String COMMENT_DELETE = "DELETE FROM COMMONS_COMMENT WHERE ID = ?";
    private static final String POST_UPDATE = "UPDATE COMMONS_POST SET CONTENT = ?, MODIFIED_DATE = ?, RELEASE_DATE = ? WHERE ID = ?";
    private static final String POST_INSERT = "INSERT INTO COMMONS_POST VALUES (?,?,?,?,?,?,?)";
    private static final String POST_DELETE = "DELETE FROM COMMONS_POST WHERE ID = ?";
    private static final String POST_LIKE = "INSERT INTO COMMONS_LIKE VALUES (?,?,?,?)";
    private static final String LIKE_GET = "SELECT * FROM COMMONS_LIKE WHERE POST_ID = ? AND USER_ID=?";
    private static final String LIKE_UPDATE = "UPDATE COMMONS_LIKE SET VOTE = ?, MODIFIED_DATE = ? WHERE USER_ID = ? AND POST_ID = ?";
    private static final String LIKE_COUNT = "SELECT COUNT(POST_ID) FROM COMMONS_LIKE WHERE POST_ID = ? AND VOTE = 1";
    private static final String LIKE_USER = "SELECT * FROM COMMONS_LIKE WHERE USER_ID = ? AND VOTE = 1";
    private static final String LIKES_FOR_POST = "SELECT * FROM COMMONS_LIKE WHERE POST_ID = ? AND VOTE = 1";
    private static final String COMMONS_POST_DELETE = "DELETE FROM COMMONS_COMMONS_POST WHERE POST_ID = ?";
    private static final String COMMENTS_DELETE = "DELETE FROM COMMONS_COMMENT WHERE POST_ID = ?";

    private SakaiProxy sakaiProxy;
    private ServerConfigurationService serverConfigurationService;
    private SqlService sqlService;

    public void init() {

        if (serverConfigurationService.getBoolean("auto.ddl", true)) {
            sqlService.ddl(this.getClass().getClassLoader(), "commons_tables");
        }
    }

    public boolean postExists(String postId) {

        log.debug("postExists({})", postId);

        List<Post> posts = sqlService.dbRead(POST_SELECT
                , new Object[] {postId}
                , new SqlReader<Post>() {
                    public Post readSqlResultRecord(ResultSet result) {
                        return new Post();
                    }
                });

        return posts.size() > 0;
    }

    public List<Post> getAllPost(final QueryBean query) throws Exception {
        return getAllPost(query, false);
    }

    public List<Post> getAllPost(final QueryBean query, boolean populate) throws Exception {

        log.debug("getAllPost({})", query);

        if (query.getEmbedder().equals(CommonsConstants.SOCIAL)) {
            int numFromIds = query.getFromIds().size();
            if (numFromIds > 0) {
                String sql = SOCIAL_COMMONS_POSTS_SELECT;
                for (int i = 0;i < numFromIds;i++) sql += "?,";
                // Trim off the trailing comma
                sql = sql.substring(0,sql.length() - 1);
                sql += ") ORDER BY CREATED_DATE DESC"; 
                List<String> params = new ArrayList<String>();
                params.add(query.getCommonsId());
                params.addAll(query.getFromIds());
                return sqlService.dbRead(sql
                        , params.toArray()
                        , new SqlReader<Post>() {
                            public Post readSqlResultRecord(ResultSet result) {
                                return loadPostFromResult(result, populate);
                            }
                        });
            } else {
                log.warn("SOCIAL posts requested, but no connection ids supplies. Returning an empty list ...");
                return new ArrayList<Post>();
            }
        } else {
            return sqlService.dbRead(COMMONS_POSTS_SELECT
                    , new Object[] {query.getCommonsId()}
                    , new SqlReader<Post>() {
                        public Post readSqlResultRecord(ResultSet result) {
                            return loadPostFromResult(result, populate);
                        }
                    });
        }
    }

    public Optional<Comment> getComment(String commentId) {

        List<Comment> comments = sqlService.dbRead(COMMENT_SELECT, new Object[] { commentId }, new SqlReader<Comment>() {
                public Comment readSqlResultRecord(ResultSet result) {
                    try {
                        return new Comment(result);
                    } catch (SQLException sqle) {
                        log.error("Failed to get comment", sqle);
                        return null;
                    }
                }
            });

        if (comments.size() > 0) {
            Comment comment = comments.get(0);
            comment.setPost(comment.getPost());
            User user = sakaiProxy.getUser(comment.getCreatorId());
            if (user != null ) {
                comment.setCreatorDisplayName(user.getDisplayName());
                comment.setCreatorUserName(user.getEid());
            }
            return Optional.of(comment);
        } else {
            log.warn("No comment for id {}", commentId);
            return Optional.empty();
        }
    }

    public Comment saveComment(Comment comment) {

        if ("".equals(comment.getId())) {
            comment.setId(UUID.randomUUID().toString());
            sqlService.dbWrite(COMMENT_INSERT
                , new Object[] { comment.getId()
                                    , comment.getPostId()
                                    , comment.getContent()
                                    , comment.getCreatorId()
                                    , new Timestamp(comment.getCreatedDate())
                                    , new Timestamp(comment.getModifiedDate()) });
        } else {
            sqlService.dbWrite(COMMENT_UPDATE
                , new Object[] { comment.getContent()
                                    , new Timestamp(comment.getModifiedDate())
                                    , comment.getId() });
        }

        return getComment(comment.getId()).get();
    }

    public boolean deleteComment(String commentId) {

        sqlService.dbWrite(COMMENT_DELETE, new Object[] { commentId });
        return true;
    }

    public Post savePost(Post post) {

        log.debug("savePost()");

        if (postExists(post.getId())) {
            sqlService.dbWrite(POST_UPDATE
                , new Object[] { post.getContent()
                                    , Timestamp.from(Instant.now())
                                    , new Timestamp(post.getReleaseDate())
                                    , post.getId() });

        } else {
            Runnable transaction = new Runnable() {

                public void run() {

                    // Test if the commons exists.
                    if (getCommons(post.getCommonsId()) == null) {
                        // Commons doesn't exist yet. Create it.
                        String embedder = post.getEmbedder();
                        String siteId = (embedder.equals(CommonsConstants.SOCIAL)) ? CommonsConstants.SOCIAL : post.getSiteId();
                        sqlService.dbWrite(COMMONS_INSERT
                            , new Object [] { post.getCommonsId(), siteId, embedder });
                    }

                    post.setId(UUID.randomUUID().toString());
                    sqlService.dbWrite(POST_INSERT
                        , new Object [] { post.getId()
                                            , post.getContent()
                                            , post.getCreatorId()
                                            , new Timestamp(post.getCreatedDate())
                                            , new Timestamp(post.getModifiedDate())
                                            , new Timestamp(post.getReleaseDate())
                                            , post.isPriority()});
                    sqlService.dbWrite(COMMONS_POST_INSERT
                        , new Object [] { post.getCommonsId(), post.getId() });
                }
            };
            sqlService.transact(transaction, "COMMONS_POST_CREATION_TRANSACTION");
        }

        return getPost(post.getId(), false);
    }

    public boolean deletePost(Post post) {

        log.debug("deletePost({})", post.getId());

        Runnable transaction = new Runnable() {

            public void run() {

                Object[] params = new Object [] { post.getId() };
                sqlService.dbWrite(COMMENTS_DELETE, params);
                sqlService.dbWrite(COMMONS_POST_DELETE, params);
                sqlService.dbWrite(POST_DELETE, params);
            }
        };

        return sqlService.transact(transaction, "COMMONS_POST_DELETION_TRANSACTION");
    }

    public boolean likePost(String postId, String userId){
        PostLike likeNow = getLike(postId, userId);
        if(likeNow == null){ //if there is no existing Like, write a new one
            sqlService.dbWrite(POST_LIKE, new Object[]{userId, postId, true, new Timestamp(new Date().getTime())});
            return true;
        }
        sqlService.dbWrite(LIKE_UPDATE, new Object[]{!likeNow.isLiked(), new Timestamp(new Date().getTime()), userId, postId});
        return true;
    }

    public PostLike getLike(String postId, String userId){
        List<PostLike> results = sqlService.dbRead(LIKE_GET, new Object[]{postId, userId}, new SqlReader<PostLike>(){
                public PostLike readSqlResultRecord(ResultSet result) {
                    return loadPostLikeFromResult(result);
                }
        });
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        return results.get(0);
    }

    public int countPostLikes(String postId){
        Integer likes =  sqlService.dbRead(LIKE_COUNT, new Object[]{postId}, new SqlReader<Integer>(){
            public Integer readSqlResultRecord(ResultSet result) {
                try{
                    return result.getInt("COUNT(POST_ID)");
                } catch (SQLException s){
                    log.error("Failed to count likes for post: {}", postId, s);
                    return null;
                }
            }
        }).get(0);
        return (likes == null) ? 0 : likes;
    }

    public int doesUserLike(String postId, String userId){
        PostLike likeRecord = getLike(postId, userId);
        return likeRecord != null && likeRecord.isLiked() ? 1 : 0;
    }

    public List<PostLike> getAllUserLikes(String userId){
        List<PostLike> results = sqlService.dbRead(LIKE_USER, new Object[]{userId}, new SqlReader<PostLike>(){
            public PostLike readSqlResultRecord(ResultSet result) {
                return loadPostLikeFromResult(result);
            }
        });
        return results;
    }

    public List<PostLike> getAllPostLikes(String postId){
        List<PostLike> results = sqlService.dbRead(LIKES_FOR_POST, new Object[]{postId}, new SqlReader<PostLike>(){
            public PostLike readSqlResultRecord(ResultSet result) {
                return loadPostLikeFromResult(result);
            }
        });
        return results;
    }

    public Post getPost(String postId, boolean loadComments) {

        List<Post> posts = sqlService.dbRead(POST_SELECT, new Object[] { postId }, new SqlReader<Post>() {
                public Post readSqlResultRecord(ResultSet result) {
                    return loadPostFromResult(result, loadComments);
                }
            });

        if (posts.size() < 1) {
            return null;
        } else {
            return posts.get(0);
        }
    }

    public Commons getCommons(String commonsId) {

        List<Commons> commons = sqlService.dbRead(COMMONS_SELECT
            , new Object[] {commonsId}
            , new SqlReader<Commons>() {
                public Commons readSqlResultRecord(ResultSet result) {
                    try {
                        return new Commons(result);
                    } catch (SQLException sqle) {
                        return null;
                    }
                }
            });

        if (commons.size() > 0) {
            return commons.get(0);
        } else {
            log.warn("No commons for id '{}'. Returning null ...", commonsId);
            return null;
        }
    }

    private Post loadPostFromResult(ResultSet result, boolean loadComments) {

        try {
            Post post = new Post(result);

            User user = sakaiProxy.getUser(post.getCreatorId());
            if (user != null ) {
                post.setCreatorDisplayName(user.getDisplayName());
                post.setCreatorUserName(user.getEid());
            }

            if (loadComments) {
                List<Comment> comments = sqlService.dbRead(COMMENTS_SELECT
                        , new Object[] {post.getId()}
                        , new SqlReader<Comment>() {
                            public Comment readSqlResultRecord(ResultSet commentResult) {

                                try {
                                    Comment comment = new Comment(commentResult);
                                    User user = sakaiProxy.getUser(comment.getCreatorId());
                                    if (user != null ) {
                                        comment.setCreatorDisplayName(user.getDisplayName());
                                        comment.setCreatorUserName(user.getEid());
                                    }
                                    String toolId = sakaiProxy.getCommonsToolId(post.getSiteId());
                                    String url = sakaiProxy.getPortalUrl() + "/directtool/"
                                                            + toolId + "?state=post&postId=" + post.getId();
                                    comment.setUrl(url);
                                    return comment;
                                } catch (SQLException sqle) {
                                    log.error("Failed to read comment from DB.", sqle);
                                    return null;
                                }
                            }
                        });
                comments.forEach(c -> c.setPost(post));
                post.setComments(comments);
            }
            return post;
        } catch (SQLException sqle) {
            log.error("Failed to read post from DB.", sqle);
            return null;
        }
    }

    private PostLike loadPostLikeFromResult(ResultSet result){
        PostLike likeNow = new PostLike();
        try{
            likeNow.setModified(new Timestamp(result.getTimestamp("MODIFIED_DATE", Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")))).getTime()));
            likeNow.setUserId(result.getString("USER_ID"));
            likeNow.setPostId(result.getString("POST_ID"));
            likeNow.setLiked(result.getBoolean("VOTE"));
        } catch (SQLException sqle) {
            log.error("Failed to read post from DB.", sqle);
            return null;
        }
        return likeNow;
    }
}
