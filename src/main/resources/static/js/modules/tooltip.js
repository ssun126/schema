class CUI_Tooltip {
    constructor(element) {
        this.element = element;
        this.title = element.getAttribute('data-tooltip-title');
        this.position = element.getAttribute('data-tooltip-position');
        this.space = this.getCustomSpace(element.getAttribute('data-tooltip-space'));
        this.safeArea = 4;

        this.initTooltip();
    }

    getCustomSpace(spaceAttribute) {
        return spaceAttribute !== null && !isNaN(spaceAttribute) ? parseInt(spaceAttribute, 10) : 7;
    }

    initTooltip() {
        this.setupEventListeners(this.element);
    }

    removeExistingTooltip() {
        const existingTooltips = document.querySelectorAll('.cui_tooltip');
        if (existingTooltips.length > 0) {
            existingTooltips.forEach(existingTooltip => {
                existingTooltip.remove();
            })
        }
    }

    createTooltipElement() {
        this.tooltipElement = document.createElement('div');
        this.tooltipElement.classList.add('cui_tooltip');

        const span = document.createElement('span');
        span.innerText = this.title;
        this.tooltipElement.appendChild(span);

        document.body.appendChild(this.tooltipElement);
        this.positionTooltip(this.tooltipElement);
    }

    positionTooltip(element) {
        const { innerWidth: windowWidth, innerHeight: windowHeight } = window;
        const tooltipWidth = element.offsetWidth;
        const tooltipHeight = element.offsetHeight;

        if (this.position === 'left' || this.position === 'right') {
            this.positionHorizontally(this.position, windowWidth, tooltipHeight);
        } else {
            this.positionVertically(this.position || 'bottom', windowWidth, tooltipWidth, windowHeight);
        }
    }

    positionVertically(direction, windowWidth, tooltipWidth, windowHeight) {
        this.rect = this.element.getBoundingClientRect();
        const rectX = parseInt(this.rect.left);
        const rectWidth = parseInt(this.rect.width / 2);
        const tooltipWidthHalf = parseInt(tooltipWidth / 2);

        let left = parseInt(rectX + rectWidth - tooltipWidthHalf);
        let c = tooltipWidthHalf;

        if (left < this.safeArea) {
            c = this.rect.left + this.rect.width / 2 - this.safeArea;
            left = this.safeArea;
        }

        if (left + tooltipWidth > windowWidth - this.safeArea) {
            c = tooltipWidth - (windowWidth - (this.rect.left + this.rect.width) + this.rect.width / 2 - this.safeArea);
            left = null;
        }

        this.tooltipElement.style.left = left !== null ? `${left}px` : 'auto';
        this.tooltipElement.style.right = left === null ? '4px' : 'auto';
        this.tooltipElement.style.setProperty('--c', `${c}px`);

        if (direction === 'top') {
            this.tooltipElement.style.bottom = `${windowHeight - this.rect.top + this.space}px`;
            this.tooltipElement.classList.add('tooltip_top');
        } else {
            console.log(this.rect);
            this.tooltipElement.style.top = `${this.rect.top + this.rect.height + this.space}px`;
            this.tooltipElement.classList.add('tooltip_bottom');
        }
    }

    positionHorizontally(direction, windowWidth, tooltipHeight) {
        this.tooltipElement.style.top = `${this.rect.top + this.rect.height / 2 - tooltipHeight / 2}px`;

        if (direction === 'left') {
            this.tooltipElement.style.right = `${windowWidth - this.rect.left + this.space}px`;
            this.tooltipElement.classList.add('tooltip_left');
        } else {
            this.tooltipElement.style.left = `${this.rect.left + this.rect.width + this.space}px`;
            this.tooltipElement.classList.add('tooltip_right');
        }
    }


    setupEventListeners(element) {
        element.addEventListener("mouseenter", () => this.createTooltipElement(element));
        element.addEventListener("mouseleave", () => this.removeExistingTooltip());
        window.addEventListener("scroll", () => this.removeExistingTooltip());
        window.addEventListener("resize", () => this.removeExistingTooltip());
    }
}

const tooltips = document.querySelectorAll('[data-tooltip-title]');
tooltips.forEach(tooltip => {
    new CUI_Tooltip(tooltip);
})