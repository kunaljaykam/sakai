/*
 * Copyright (c) 2003-2021 The Apereo Foundation
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
package org.sakaiproject.conversations.api.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Lob;

import org.hibernate.annotations.GenericGenerator;

import org.sakaiproject.springframework.data.PersistableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CONV_COMMENTS", indexes = { @Index(name = "conv_comments_post_idx", columnList = "POST_ID"),
                                        @Index(name = "conv_comments_site_idx", columnList = "SITE_ID"),
                                        @Index(name = "conv_comments_topic_idx", columnList = "TOPIC_ID") })
@Getter
@Setter
public class ConversationsComment implements PersistableEntity<String> {

    @Id
    @Column(name = "COMMENT_ID", length = 36, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "SITE_ID", length = 99, nullable = false)
    private String siteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private ConversationsPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOPIC_ID")
    private ConversationsTopic topic;

    @Lob
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "LOCKED")
    private Boolean locked = Boolean.FALSE;

    @Embedded
    private Metadata metadata;
}
