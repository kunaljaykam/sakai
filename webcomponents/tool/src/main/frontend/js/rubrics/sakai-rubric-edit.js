import {RubricsElement} from "./rubrics-element.js";
import {html} from "/webcomponents/assets/lit-element/lit-element.js";
import {tr} from "./sakai-rubrics-language.js";

export class SakaiRubricEdit extends RubricsElement {

  constructor() {

    super();

    this.rubricClone = {};
  }

  static get properties() {

    return {
      rubric: { type: Object }
    };
  }

  attributeChangedCallback(name, oldValue, newValue) {

    super.attributeChangedCallback(name, oldValue, newValue);

    if (name === "rubric") {
      this.rubricClone = JSON.parse(newValue);
      if (this.rubricClone.new) {
        this.updateComplete.then(() => this.querySelector(".edit").click() );
      }
    }
  }

  firstUpdated() {

    const trigger = this.querySelector('.edit');

    this.popover = new bootstrap.Popover(trigger, {
      content: this.querySelector(`#edit_rubric_${this.rubric.id}`),
      html: true
    });

    // When shown, set focus to the title editor input
    trigger.addEventListener("shown.bs.popover", () => {
      document.getElementById(`rubric_title_edit_${this.rubric.id}`)?.focus();
    });
  }

  _eatEvent(e) {
    e.stopPropagation();
  }

  _editRubric(e) {
    e.stopPropagation();
  }

  _cancelEdit(e) {

    e.stopPropagation();
    this.rubricClone.title = this.rubric.title;
    document.getElementById(`rubric_title_edit_${this.rubric.id}`).value = this.rubric.title;
    this.popover.hide();
  }

  _saveEdit(e) {

    e.stopPropagation();
    const title = document.getElementById(`rubric_title_edit_${this.rubric.id}`).value;
    this.dispatchEvent(new CustomEvent("update-rubric-title", { detail: title }));
    this.popover.hide();
  }

  render() {

    return html`
      <button id="edit-rubric-button-${this.rubric.id}" class="linkStyle edit"
          data-bs-toggle="popover"
          @click="${this._editRubric}"
          aria-haspopup="true"
          aria-expanded="false"
          aria-controls="edit_rubric_${this.rubric.id}"
          title="${tr("edit_rubric")} ${this.rubric.title}"
          aria-label="${tr("edit_rubric")} ${this.rubric.title}">
        <i class="bi-pencil-fill pe-none"></i>
      </button>

      <div id="edit_rubric_${this.rubric.id}" @click="${this._eatEvent}">
        <div class="popover-title">
          <div class="buttons act">
            <button class="active save" @click="${this._saveEdit}">
              <sr-lang key="save">Save</sr-lang>
            </button>
            <button class="btn btn-link btn-xs cancel" @click="${this._cancelEdit}">
              <sr-lang key="cancel">Cancel</sr-lang>
            </button>
          </div>
        </div>
        <div class="popover-content form">
          <div class="form-group">
            <label for="rubric_title_edit">
              <sr-lang key="rubric_title">Rubric Title</sr-lang>
            </label>
            <input title="${tr("rubric_title")}" id="rubric_title_edit_${this.rubric.id}" type="text" class="form-control" value="${this.rubricClone.title}" maxlength="255">
          </div>
        </div>
      </div>
    `;
  }
}

if (!customElements.get("sakai-rubric-edit")) {
  customElements.define("sakai-rubric-edit", SakaiRubricEdit);
}
