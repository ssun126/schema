/*  삼성디스플레이용 common.js 
*   2015.06.12 조성진 최초 생성.
*   
*/

if (sessionStorage.getItem("LANGUAGE")) {
    $('html').attr('data-language', sessionStorage.getItem("LANGUAGE"));
}

function ajaxService(dataString, url, successFn, method) {
    if (!method) method = "POST";
    var data = dataString ? JSON.stringify(dataString) : "";
    $.ajax({
        method: method,
        url: url,
        cache: false,
        data: data,
        contentType: "application/json; charset=utf-8",
        processData: false,
        dataType: "json",
        success: successFn
    });
}

var formData = false;

function updatePagerIcons(table) {
    var replacement = {
        'ui-icon-seek-first': 'fa fa-angle-double-left bigger-140',
        'ui-icon-seek-prev': 'fa fa-angle-left bigger-140',
        'ui-icon-seek-next': 'fa fa-angle-right bigger-140',
        'ui-icon-seek-end': 'fa fa-angle-double-right bigger-140'
    };
    $(
    '.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon')
    .each(
        function () {
            var icon = $(this);
            var $class = $.trim(icon.attr('class').replace(
                'ui-icon', ''));

            if ($class in replacement)
                icon.attr('class', 'ui-icon '
                    + replacement[$class]);
        });
}

function initCap(str) {
    var str = str.substring(0, 1).toUpperCase() + str.substring(1, str.length).toLowerCase();
    return str;
}
function rgb2hex(rgb) {
    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    return "#" +
     ("0" + parseInt(rgb[1], 10).toString(16)).slice(-2) +
     ("0" + parseInt(rgb[2], 10).toString(16)).slice(-2) +
     ("0" + parseInt(rgb[3], 10).toString(16)).slice(-2);
}

function string2utf8bytearray(s) {
    // We need to allocate at least one byte per character.
    var output = new Array(s.length);
    for (var i = 0, j = 0; i < s.length; i++) {
        var c = s.charCodeAt(i);
        if (c < 0x7F) {
            output[j++] = c;
        } else if (c < 0x7FF) {
            output[j++] = 0xC0 + (c >> 6);
            output[j++] = 0x80 + (c & 0x3F);
        } else if (c < 0xFFFF) {
            output[j++] = 0xE0 + (c >> 12);
            output[j++] = 0x80 + ((c >> 6) & 0x3F);
            output[j++] = 0x80 + (c & 0x3F);
        } else if (c < 0x10FFFF) {
            output[j++] = 0xF0 + (c >> 18);
            output[j++] = 0x80 + ((c >> 6) & 0x3F);
            output[j++] = 0x80 + ((c >> 12) & 0x3F);;
            output[j++] = 0x80 + (c & 0x3F);
        }
    }
    return output;
}
var d3ChartHeight = 0;

/*
function setCookie(cookieName, value, exdays) {
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var cookieValue = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toGMTString());
    document.cookie = cookieName + "=" + cookieValue;
}

function deleteCookie(cookieName) {
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}

function getCookie(cookieName) {
    cookieName = cookieName + '=';
    var cookieData = document.cookie;
    var start = cookieData.indexOf(cookieName);
    var cookieValue = '';
    if (start != -1) {
        start += cookieName.length;
        var end = cookieData.indexOf(';', start);
        if (end == -1) end = cookieData.length;
        cookieValue = cookieData.substring(start, end);
    }
    return unescape(cookieValue);
}
*/
function authCheck() {
    $.get("/SessionTest/SessionCheck",
        function (data) {
            //console.debug(data.result);
            if (!data.result.flag) {
                $(location).attr("href", "/");
            } else {
                menuStartSet(data.result);
            }
        }
    );
}

function menuStartSet(data) {
    var dataList = data.data;
    for (var i in dataList) {
        dataList[i].parentid = dataList[i].parentId;
    }
    var $native = $("#jqxmenu");
    $native.css("background", "rgba(0,0,0,0)");
    $native.css("border-color", "rgba(0,0,0,0)");
    $native.css("color", "rgba(255, 255, 255, 0.8)");
    var h = $native.height();
    h = "100%";
    var w = "100%";
    if (h < 1) {
        h = 30;
    }
    var source =
        {
            datatype: "json",
            datafields: [
                { name: 'id' },
                { name: 'parentid' },
                { name: 'text' },
                { name: 'subMenuWidth' }
            ],
            id: 'id',
            localdata: dataList
        };
    // create data adapter.
    var dataAdapter = new $.jqx.dataAdapter(source);
    // perform Data Binding.
    dataAdapter.dataBind();
    // get the menu items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
    // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
    // specifies the mapping between the 'text' and 'label' fields.  
    var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label' }]);
    $($native).jqxMenu({ source: records, height: h, width: w });
    //$($native).on('itemclick', function (event) {
    //    //$("#eventLog").text("Id: " + event.args.id + ", Text: " + $(event.args).text());
    //});

    for (var i = 0; i < dataList.length; i++) {
        if (!(dataList[i].url == null || dataList[i].url == "")) {
            $("li[item-label='" + dataList[i].text + "'][id='" + dataList[i].id + "']").attr("url", dataList[i].url).click(function () {
            //$("li[item-label='" + dataList[i].text + "'][id='" + dataList[i].id + "']").click(function () {
                $(location).attr("href", $(this).attr("url"));
            });
            //$("li[item-label='16g fat'][id='16']").css("background", "#990000");
        }
    }
    $(".jqx-menu-popup").css("color", "black");
}

var micaCommon = {
    download: function (contentDom, type, width, height, plusWidth, plusHeight) {
        if (typeof contentDom == "string") {
            contentDom = $(contentDom);
        }
        type = type ? type : "png";
        width = width ? width : contentDom.width();
        height = height ? height : contentDom.height() + 10;

        width = plusWidth ? width + plusWidth : width;
        height = plusHeight ? height + plusHeight : height;
        var bodyString = contentDom.html();
        //var style = $("<div></div>").html($("head").find("link").clone()).html();
        var link = $("link");
        var htmlBody = "<!DOCTYPE html><html><head><meta charset='utf-8'>#{style}</head><body>#{body}</body></html>";
        var tagLink = "";
        var tag = '<link href="#{href}" rel="stylesheet" type="text/css">';
        for (var i = 0; i < link.length; i++) {

            //var hrefArray = $("link")[i].href.replace("://", "").split("/");
            //var href = "";
            //for (var j = 1; j < hrefArray.length; j++) {
            //    href += hrefArray[j];
            //}
            //tagLink += tag.replace(/#{href}/gi, href);
            tagLink += tag.replace(/#{href}/gi, $("link")[i].href);
        }
        var style = tagLink;

        htmlBody = htmlBody.replace(/#{style}/gi, style);
        htmlBody = htmlBody.replace(/#{body}/gi, bodyString);
        $("body").css("cursor", "progress");
        //e.preventDefault();
        var d = document.createElement("form");
        var url = "/MICAApi/PrintSubmitForm";
        d.setAttribute("method", "post");
        d.setAttribute("action", url);

        var h = document.createElement("input");
        h.setAttribute("type", "hidden");
        h.setAttribute("name", "type");
        h.setAttribute("value", type);
        d.appendChild(h);
        h = null;

        //var tg = "<div>success Print File</div>";
        h = document.createElement("input");
        h.setAttribute("type", "hidden");
        h.setAttribute("name", "html");
        //var style = "<link href='/Content/css/site-designer.css' rel='stylesheet'> <link rel='stylesheet' type='text/css' href='https://fonts.googleapis.com/css?family=Bitter:400,700,400italic|Changa One:400,400italic|Droid Sans:400,700|Droid Serif:400,700|Exo:100,200,300,400,500,600,700,800,900,100italic,200italic,300italic,400italic,500italic,600italic,700italic,800italic,900italic|Great Vibes:400|Inconsolata:400,700,400italic,700italic|Lato:100,200,300,400,700,900,100italic,200italic,300italic,400italic,700italic,900italic|Merriweather:300,400,700,900|Montserrat:400,700|Open Sans:300,400,600,700,800,300italic,400italic,600italic,700italic,800italic|Oswald:300,400,700|PT Sans:400,700,400italic,700italic|PT Serif:400,700,400italic,700italic|Ubuntu:300,400,500,700,300italic,400italic,500italic,700italic|Varela:400|Varela Round:400|Vollkorn:400,700,400italic,700italic&amp;subset='><link rel='stylesheet' type='text/css' href='/Content/css/site.css'><link rel='stylesheet' type='text/css' href='/Content/jqwidgets/styles/jqx.base.css'><link href='/theme/js/plugins/d3js/nv.d3.css' rel='stylesheet' type='text/css'><link rel='stylesheet' type='text/css' href='test3333.css'>";
        h.setAttribute("value", encodeURIComponent(htmlBody));
        d.appendChild(h);
        h = null;

        h = document.createElement("input");
        h.setAttribute("type", "hidden");
        h.setAttribute("name", "height");
        //h.setAttribute("value", 4000);
        //h.setAttribute("value", $("#jqgrid").height() + 20);
        h.setAttribute("value", height);
        d.appendChild(h);
        h = null;

        h = document.createElement("input");
        h.setAttribute("type", "hidden");
        h.setAttribute("name", "width");
        //h.setAttribute("value", 1420);
        //h.setAttribute("value", $("body").width());
        h.setAttribute("value", width);

        d.appendChild(h);
        h = null;

        document.body.appendChild(d);
        d.submit();
        document.body.removeChild(d);
        $("body").css("cursor", "default");
    }
}



// 사용 예시
// QueryString.id
// 없는 경우는 undefined
var QueryString = function () {
    // This function is anonymous, is executed immediately and 
    // the return value is assigned to QueryString!
    var query_string = {};
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        // If first entry with this name
        if (typeof query_string[pair[0]] === "undefined") {
            query_string[pair[0]] = decodeURIComponent(pair[1]);
            // If second entry with this name
        } else if (typeof query_string[pair[0]] === "string") {
            var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
            query_string[pair[0]] = arr;
            // If third or later entry with this name
        } else {
            query_string[pair[0]].push(decodeURIComponent(pair[1]));
        }
    }
    return query_string;
}();

//function ajaxService(dataString, url, successFn, method) {
//    if (!method) method = "POST";
//    var data = dataString ? JSON.stringify(dataString) : "";
//    $.ajax({
//        method: method,
//        url: url,
//        cache: false,
//        data: data,
//        contentType: "application/json; charset=utf-8",
//        processData: false,
//        dataType: "json",
//        success: ajaxServiceProcess.ajaxSuccess(successFn),
//        error: ajaxServiceProcess.ajaxFail(failFn)
//    });
//}

//var ajaxServiceProcess = {
//    ajaxSuccess: function (successFn) {
//        if (successFn !== undefined)
//            successFn;
//    },
//    ajaxFail: function (jqXHR, textStatus, error) {
//        console.log(jqXHR);
//    },
//}



/**
 * $.parseParams - parse query string paramaters into an object.
 * var params = $.parseParams("id=123&name=test");  -> array 반환
 */
(function ($) {
    var re = /([^&=]+)=?([^&]*)/g;
    var decodeRE = /\+/g;  // Regex for replacing addition symbol with a space
    var decode = function (str) { return decodeURIComponent(str.replace(decodeRE, " ")); };
    $.parseParams = function (query) {
        var params = {}, e;
        while (e = re.exec(query)) {
            var k = decode(e[1]), v = decode(e[2]);
            if (k.substring(k.length - 2) === '[]') {
                k = k.substring(0, k.length - 2);
                (params[k] || (params[k] = [])).push(v);
            }
            else params[k] = v;
        }
        return params;
    };
})(jQuery);