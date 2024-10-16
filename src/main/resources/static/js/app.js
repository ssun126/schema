// ㅜㅜ import 쓰고 싶다....
const scripts = ["gnb", "lnb", "tooltip","tab"];

document.addEventListener('DOMContentLoaded', () => {
    scripts.forEach(name => {
        let script = document.createElement("script");
        script.src = `../../js/modules/${name}.js`;
        document.body.appendChild(script);
    })
});


