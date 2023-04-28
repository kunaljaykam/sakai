class SidebarCollapseButton {

  get collapsed() {
    return this._element.getAttribute("data-portal-sidebar-collapsed") === "true" ? true : false;
  }

  set collapsed(newValue) {

    this._element.setAttribute("data-portal-sidebar-collapsed", newValue);
    this.setCollapsed(newValue)
  }

  get title() {
    return this._element.getAttribute("title");
  }

  set title(newValue) {

    this._element.setAttribute("title", newValue);
    this._tooltip.setContent({'.tooltip-inner': newValue});
  }

  constructor(element, config) {

    this._i18n = config?.i18n;
    this._toggleClass = config?.toggleClass;
    this._portalContainer = config?.portalContainer;
    this._sitesSidebar = config?.sitesSidebar;
    this._element = element;
    this._element.addEventListener("click", this.toggle.bind(this));
    this._tooltip = bootstrap.Tooltip.getOrCreateInstance(this._element);
  }

  toggle() {

    this.collapsed = !this.collapsed;
    const iconSpan = this._element.querySelector("span");
    iconSpan.classList.remove("bi-chevron-double-left", "bi-chevron-double-right");
    iconSpan.classList.add(`bi-chevron-double-${this.collapsed ? "right" : "left"}`);
    this._portalContainer.classList.toggle(this._toggleClass, this.collapsed);
    this._sitesSidebar.classList.toggle(this._toggleClass, this.collapsed);
    this.title = this.collapsed ? this._i18n.titleCollapsed : this._i18n.titleExpanded;
  }

  async setCollapsed(collapsed) {

    collapsed = collapsed ? "true" : "false";
    const putReq = await fetch(`/direct/userPrefs/updateKey/${portal.user.id}/sakai:portal:sitenav?toolsCollapsed=${collapsed}`, { method: "PUT" });
    if (!putReq.ok) {
      console.error(`Could not set collapsed state "${collapsed}" for sidebar.`);
    }
  }
}
