var ajaxService = function (data, url, successFn, method, errorFn, options) {
    // token
    ajaxService.setAuth();

    method = method || "POST";
    options = options || {};
    var async = options.async != null ? options.async : true;
    var contentType = options.contentType != null ? options.contentType : "application/json; charset=utf-8";
    var processData = options.processData != null ? options.processData : false;
    var dataType = options.dataType != null ? options.dataType : "json";
    var reqData = typeof data == "object" ? JSON.stringify(data) : data;
    return $.ajax({
        method: method,
        url: url,
        cache: false,
        data: reqData,
        async: async,
        contentType: contentType,
        processData: processData,
        dataType: dataType,
        success: ajaxServiceProcess.ajaxSuccess(successFn, url),
        error: function (xhr, stauts, ex) {
            //20240405 토큰 만료시간 설정으로 인한 에러 표시 추가
            switch (xhr.status){
                case 400:
                    window.location = '/error.html?M_ExitSession';
                    return;
                case 401:
                    window.location = '/error.html?M_ExitSession';
                    return;
            }
            ajaxServiceProcess.ajaxError(errorFn, url)
        }
    });
}

ajaxService.setAuth = function () {

        var authorization = ajaxService.setAuthToken();
        if (authorization != "") {
            $.ajaxSetup({
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", authorization);
                },
                error: function (xhr, stauts, ex) {
                    if (xhr.status == 401)
                    {
                    	window.location = '/error.html?M_NotAuthorization';
                        return;
                    }
                    ajaxServiceProcess.ajaxError(errorFn, url)
                },
                global: true
            });
        }
}

ajaxService.clearAuth = function () {

    var authorization = ajaxService.setAuthToken();
    if (authorization != "") {
        $.ajaxSetup({
            beforeSend: function (xhr) {
            },
            global: true
        });
    }
}

ajaxService.setAuthToken = function () {
    var authorization = "";

    if (sessionStorage.getItem('authorization')) {
        var auth = JSON.parse(sessionStorage.getItem('authorization'));
        authorization = auth.token_type + " " + auth.access_token;
    }
    return authorization;
}

var ajaxServiceProcess = {
    ajaxSuccess: function (successFn, url) {
        if (successFn != null) {
            return successFn;
        } else {
            //console.log("NotSuccessFn url : " + url);
        }
    },
    ajaxError: function (errorFn, url) {
        if (errorFn != null) {
            return errorFn;
        } else {
            //console.log("NotErrorFn url : " + url);
        }
    }
}
