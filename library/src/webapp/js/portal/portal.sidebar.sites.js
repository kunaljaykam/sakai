class SitesSidebar {

  constructor(element, config) {

    this._i18n = config?.i18n;
    this._element = element;
    this._lessonsSubpageData = config?.lessonsSubpageData;
    this._pinnedSiteList = document.getElementById("pinned-site-list");
    this._recentSiteList = document.getElementById("recent-site-list");

    const sitesListItems = element.querySelectorAll(".site-list-item");

    const pinButtonElements = element.querySelectorAll(".site-opt-pin");
    pinButtonElements.forEach(buttonEl => new PinButton(buttonEl, { i18n: this._i18n?.pinButtons}));

    element.querySelectorAll(".site-description-button").forEach(buttonEl => new bootstrap.Popover(buttonEl));

    document.addEventListener("site-pin-change", this.handlePinChange.bind(this));

    element.querySelectorAll(".site-list-item-collapse").forEach(btn => {

      const chevron = element.querySelector(`[data-bs-target='#${btn.id}'] > i`);

      chevron.className = `bi-chevron-${btn.classList.contains("show") ? "down" : "right"}`;

      btn.addEventListener("show.bs.collapse", e => {

        e.stopPropagation();
        chevron.classList.replace("bi-chevron-right", "bi-chevron-down");
      });
      btn.addEventListener("hide.bs.collapse", e => {

        e.stopPropagation();
        chevron.classList.replace("bi-chevron-down", "bi-chevron-right");
      });
    });
  }

  setView(mobile) {

    const mobileClasses = ["portal-nav-sidebar-mobile", "offcanvas", "offcanvas-start"];
    const desktopClasses = ["portal-nav-sidebar-desktop"];

    this._element.style.visibility = "hidden";
    if (mobile) {
      //Set mobile view
      this._element.classList.add(...mobileClasses);
      this._element.classList.remove(...desktopClasses);
      this._element.addEventListener("hidden.bs.offcanvas", () => {
        this._element.style.visibility = "visible";
      }, { once: true })
      this._element.addEventListener("show.bs.offcanvas", () => {
        this._element.style.visibility = "visible";
      }, { once: true })
    } else {
      //Set desktop view
      
      //Check if we can find an offcanvas instance and dispose it
      bootstrap.Offcanvas.getInstance(this._element)?.dispose();

      this._element.classList.remove(...mobileClasses);
      this._element.classList.add(...desktopClasses);
      this._element.style.visibility = "visible";
    }
    this._element.classList.remove("d-none");
  }

  async handlePinChange(event) {

    const pinButton = event.target;
    const pinned = event.detail.pinned;
    const siteId = event.detail.siteId;
    const timeStamp = new Date().valueOf();

    pinButton.setAttribute("disabled", "disabled");

    const favoritesReq = await fetch(`/portal/favorites/list?_${timeStamp}`);

    if (favoritesReq.ok) {
      const favoritesValues = await favoritesReq.json();
      const alreadyPinned = favoritesValues.favoriteSiteIds.includes(siteId);

      if (pinned !== alreadyPinned) {
        const payload = JSON.parse(JSON.stringify(favoritesValues));
        if (pinned) {
          payload.favoriteSiteIds.push(siteId);
        } else {
          payload.favoriteSiteIds.splice(payload.favoriteSiteIds.indexOf(siteId), 1);
        }

        const data = new URLSearchParams();
        data.append("userFavorites", JSON.stringify(payload));

        const url = "/portal/favorites/update";
        fetch(url, {
          credentials: "include",
          headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
          method: "POST",
          body: data,
        })
        .then(r => {

          if (!r.ok) {
            throw new Error(`Network error while updating pinned sites at url ${url}`);
          } else {
            const currentItem = pinButton.closest(".site-list-item");
            if (pinned) {
              const originalLink = currentItem.querySelector("a");
              const template = document.getElementById("site-list-item-template");
              const newItem = template.content.cloneNode(true);
              newItem.querySelector("a").innerHTML = originalLink.innerHTML;
              newItem.querySelector("a").href = originalLink.href;

              const newButton = newItem.querySelector("button");
              newButton.classList.add("bi-pin-fill");
              newButton.dataset.pinned = "true";
              newButton.dataset.pinSite = siteId;

              !currentItem.classList.contains("is-current-site") && currentItem.remove();

              this._pinnedSiteList.append(newItem);
              this._pinnedSiteList.lastElementChild.dataset.site = siteId;
              this._pinnedSiteList.parentElement.classList.remove("d-none");
              new PinButton(newButton, { i18n: this._i18n?.pinButtons });
            } else {
              document.querySelectorAll(`#toolMenu button[data-pin-site="${siteId}"]`).forEach(b => {

                b.classList.remove("bi-pin-fill");
                b.dataset.pinned = "false";
                b.classList.add("bi-pin");
              });

              if (!currentItem.classList.contains("is-current-site")) {
                currentItem.remove();
              } else {
                this._pinnedSiteList.querySelector(`li[data-site="${siteId}"`).remove();
              }
              if (!this._pinnedSiteList.children.length) {
                this._pinnedSiteList.parentElement.classList.add("d-none");
              }
            }
            if (!this._recentSiteList.children.length) {
              this._recentSiteList.parentElement.classList.add("d-none");
            }
          }
        })
        .catch (error => console.error(error));
      } else {
        //Nothing to do
      }
    } else {
        console.error(`Failed to request favorites ${favoritesReq.text}`)
    }

    pinButton.removeAttribute("disabled");
  }
}

class PinButton {

  get title() {
    return this._element.title;
  }

  set title(newValue) {
    this._element.title = newValue;
  }

  get pinned() {
    return this._element.dataset.pinned === "true";
  }

  set pinned(newPinned) {
    this._element.dataset.pinned = newPinned;
  }

  constructor(element, config) {

    console.log(element);

    this._element = element;
    this._i18n = config?.i18n;
    this._site = element.dataset.pinSite;
    element.addEventListener("click", this.toggle.bind(this));
    this.title = element.dataset.pinned === "true" ? this._i18n.titleUnpin : this._i18n.titlePin;
  }

  toggle() {

    this.pinned = !this.pinned
    this.title = this.pinned ? this._i18n.titleUnpin : this._i18n.titlePin;
    this.toggleIcon()
    this.emitPinChange();
  }

  toggleIcon() {

    const buttonClasses =  this._element.classList;
    const pinnedIcon = "bi-pin";
    const unPinnedIcon = "bi-pin-fill";
    buttonClasses.toggle(pinnedIcon);
    buttonClasses.toggle(unPinnedIcon);
  }

  // Dispatches event which will cause a fetch to change pinned value
  emitPinChange() {

    const eventName = "site-pin-change";
    const eventPayload = { pinned: this.pinned, siteId: this._site };
    this._element.dispatchEvent(new CustomEvent(eventName, { detail: eventPayload, bubbles: true }));
  }
}
