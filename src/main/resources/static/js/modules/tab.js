class CUI_Tab {
    constructor(target) {
        this.tabElement = target instanceof HTMLElement ? target : false;
        if(! this.tabElement ) return false;

        this.initialize();
    }

    initialize() {
        this.setupEventListeners();
    }

    setupEventListeners () {
        const tabButton = this.tabElement.querySelectorAll('.tab_list .tab_button');
        tabButton.forEach(anchor => {
            anchor.addEventListener("click", () => this.handleEnter(anchor));
        });
    }

    handleEnter (target) {
        const parentElement = target.closest('.cui_tab');
        const targetLiElement = target.closest('li');
        const targetContent = target.dataset.tabTarget || '.tab_contents > .tab_content:first-child'
        const targetContentElement = document.querySelector(`${targetContent}`);
        const lisElement = parentElement.querySelectorAll('.tab_list > ul > li');
        const contentsElement = parentElement.querySelectorAll('.tab_contents > .tab_content');

        lisElement.forEach(li => {
            li.removeAttribute('aria-selected');
        })
        contentsElement.forEach(content => {
            content.removeAttribute('aria-expanded');
        })

        targetLiElement.setAttribute('aria-selected', 'true');
        targetContentElement.setAttribute('aria-expanded', 'true');
    }
}

const cui_tabs_Element = document.querySelectorAll('.cui_tab');
cui_tabs_Element.forEach(cui_tab_Element => {
    new CUI_Tab(cui_tab_Element);
})