$(document).ready(function(){
	main.init();
});
const main = {
	init: function() {
		this.style();
		this.event();
		this.setMultiLang();
		this.resize();
		updateLanguageContent('KR');
	},
	style: function() {
	},
	event: function() {
	},
	resize:function(){
	},
	setMultiLang : function() {
    	const data = {};
        if (navigator.appName == "Netscape") {
        	data.LANG = navigator.language;
        } else {
        	data.LANG = navigator.userLanguage;
        }

        if (data.LANG.toUpperCase() == "KO" || data.LANG.toUpperCase() == "KO-KR") {
        	data.LANG = "kr";
        } else {
        	data.LANG = "en";
        }

        sessionStorage.setItem("LANGUAGE", data.LANG);

    	$.ajax({
    		url : '/multiLanguage/setLocalStorage',
    		type : "post",
    		success : function(data) {
        		localStorage.setItem("MULTI_LANGUAGE_DATA", JSON.stringify(data));
        		//console.log("localStorage???????"+localStorage.getItem("MULTI_LANGUAGE_DATA"))

                //Schema.require("lang").init();
        		setTimeout(main.getMessage(), 500);
    		},
    		error : function() {
    			main.setMessage("");
    		}
    	});
	},
	getMessage: function(){
		const msg = window.location.search.substr(1);
		// msg = decodeURI(msg);
		//main.setMessage(schemaCommon.lang(msg));
	},
	setMessage: function(msg){
		if(msg == ""){
			$(".main-errormsg").css('display','none');
		}else{
			$(".main-errormsg").html(msg);
		}
	}
}




// 로컬스토리지에서 MULTI_LANGUAGE_DATA 가져오기
function getMultiLanguageData() {
    const data = localStorage.getItem('MULTI_LANGUAGE_DATA');
    if (data) {
        return JSON.parse(data);  // JSON 배열 반환
    }
    return null;
}

// ID와 lang을 기반으로 해당 값을 찾는 함수
function getLocalizedValue(id, lang) {
    const data = getMultiLanguageData();
    if (!data) {
        return "로컬스토리지에 데이터가 없습니다.";
    }

    // 해당 ID와 lang을 기반으로 데이터 찾기
    const item = data.find(entry => entry.ID === id);
    if (item && item[lang]) {
        return item[lang];  // 해당 lang에 맞는 값 반환
    }

    return `ID 또는 lang에 해당하는 데이터가 없습니다.`;
}

// 특정 lang에 맞는 데이터를 span에 적용하는 함수
function updateLanguageContent(lang) {
    const spans = document.querySelectorAll('span[data-type="lang"]');

    document.querySelectorAll('span[data-type="lang"]').forEach(span => {
        //console.log("span : " + span.id); // id가 존재하면 출력
        const id = span.id;
        const localizedValue = getLocalizedValue(id, lang);
        console.log("localizedValue========="+localizedValue);
        // 해당 span에 다국어 값을 넣음
        span.innerText = localizedValue;
    });
}

// 예시: lang을 'KR'로 설정하여 다국어 처리
