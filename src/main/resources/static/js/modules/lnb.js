class CUI_Lnb {
    constructor(target) {
        this.gnbElement = document.querySelector(target);
        if(! this.gnbElement ) return false;

        this.initialize();
    }

    initialize() {
        this.setupEventListeners();
    }

    setupEventListeners () {
        const elements = this.gnbElement.querySelectorAll('.nav_item');
        elements.forEach(anchor => {
            const hasAside = anchor.querySelector('.nav_aside') || false;
            if (!hasAside){
                anchor.addEventListener("click", () => this.handleEnter(anchor));
            } else {
                const button = anchor.querySelector('.nav_label');
                button.addEventListener("click", () => this.handleEnter(button));
            }
        });
    }
    handleEnter (target) {
        const parent = target.closest('li');
        const anchor = parent.querySelector('.nav_item');
        const hasToggle = anchor.querySelector('.nav_toggle') || false;
        const selected = anchor.getAttribute('aria-selected') || false;
        const expanded = parent.getAttribute('aria-expanded') || false;

        if (hasToggle) {
            if(expanded){
                parent.removeAttribute('aria-expanded');
            } else {
                parent.setAttribute('aria-expanded', 'true');
            }
        } else {
            if (selected) {
                anchor.removeAttribute('aria-selected');
            } else {
                anchor.setAttribute('aria-selected', 'true');
            }
        }
    }
}

new CUI_Lnb('.cui_nav');
