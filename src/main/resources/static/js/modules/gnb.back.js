// class cui_Gnb {
//     constructor(target) {
//         this.gnbElement = document.querySelector(target);
//         this.moreElement = this.gnbElement.querySelector('.more');
//         this.moreUlElement = this.moreElement.querySelector(':scope > ul');
//
//         this.items1depth = Array.from(this.gnbElement.querySelectorAll(':scope > ul > li:not(.more)'));
//         this.itemWidths = this.items1depth.map(item => item.offsetWidth);
//
//         this._listWidth = 0;
//         this.initialize();
//     }
//
//     initialize() {
//         this.setupEventListeners();
//         this.observeResize();
//         this.updateDimensions();
//     }
//
//     get listWidth() {
//         return this._listWidth;
//     }
//
//     set listWidth(value) {
//         this._listWidth = parseInt(value, 10);
//     }
//
//     updateDimensions() {
//         const parentElement = this.gnbElement.parentElement;
//         const totalWidth = parentElement.offsetWidth;
//         const siblingsWidth = Array.from(parentElement.children)
//             .filter(child => child !== this.gnbElement)
//             .reduce((acc, sibling) => acc + sibling.offsetWidth, 0);
//
//         this.listWidth = totalWidth - siblingsWidth;
//     }
//
//     updateToolbar() {
//         let toolbarWidth = 0;
//         this.moreUlElement.innerHTML = '';
//
//         this.items1depth.forEach((item, index) => {
//             const itemWidth = this.itemWidths[index];
//             toolbarWidth += itemWidth;
//
//             if (toolbarWidth > this.listWidth - 80) {
//                 const clone = item.cloneNode(true);
//                 clone.removeAttribute("style");
//                 this.moreUlElement.append(clone);
//                 this.applyEventListeners(clone);
//
//                 item.style.display = "none";
//                 this.moreElement.classList.add('on');
//             } else {
//                 item.removeAttribute("style");
//                 this.moreElement.classList.remove('on');
//             }
//         });
//     }
//     handleMouseEnter(anchor) {
//         const ul = anchor.closest('li')?.querySelector(':scope > ul');
//         if (ul) {
//             const anchorRect = anchor.getBoundingClientRect();
//             const ulWidth = this.measureElement(ul).width;
//             const windowScrollAreaGap = 20;
//             const ulPaddingTop = parseInt(getComputedStyle(ul).getPropertyValue('--gnb-gap'),10);
//             const is1depth = ul.parentElement.parentElement.parentElement.classList.contains('cui_gnb');
//             let left = is1depth ? anchorRect.left : anchorRect.right;
//             let top = is1depth ? anchorRect.bottom : anchorRect.top - ulPaddingTop;
//
//             left = left + ulWidth + windowScrollAreaGap > window.innerWidth ? anchorRect.left - ulWidth : left ;
//
//             left = parseInt(left,10);
//             top = parseInt(top,10);
//
//             ul.style.top = `${top}px`;
//             ul.style.left = `${left}px`;
//             ul.setAttribute('aria-expanded', 'true');
//         }
//     }
//     handleMouseLeave(anchor) {
//         const ul = anchor.closest('li')?.querySelector(':scope > ul');
//         if (ul) {
//             ul.removeAttribute('aria-expanded');
//         }
//     }
//
//
//
//     applyEventListeners(element) {
//         element.querySelectorAll('a').forEach(item => {
//             item.addEventListener("mouseenter", () => this.handleEnter(item));
//             item.addEventListener("focusin", () => this.handleEnter(item));
//             // item.addEventListener("mouseleave", () => this.handleMouseLeave(item));
//             // item.addEventListener("blur", () => this.handleTabBlur(item));
//         });
//     }
//
//     setupEventListeners() {
//         this.applyEventListeners(this.gnbElement);
//     }
//
//     observeResize() {
//         const resizeObserver = new ResizeObserver(() => {
//             this.updateDimensions();
//             this.updateToolbar();
//         });
//         resizeObserver.observe(document.body);
//     }
//
//     measureElement(element) {
//         const originalDisplay = element.style.display;
//         const originalVisibility = element.style.visibility;
//
//         element.style.display = 'block';
//         element.style.visibility = 'hidden';
//
//         const rect = element.getBoundingClientRect();
//
//         element.style.display = originalDisplay;
//         element.style.visibility = originalVisibility;
//
//         return rect;
//     }
// }
// new cui_Gnb('.cui_gnb');
//
