package com.dongwoo.SQM.common.util.interceptor;

import com.dongwoo.SQM.common.service.CommonService;
import java.util.*;
import java.util.regex.Pattern;

public class TokenAuthInterceptor {

	private Set<String> permittedURL;

	public void setPermittedURL(Set<String> permittedURL) {
		this.permittedURL = permittedURL;
	}

    private CommonService commonService;

}
