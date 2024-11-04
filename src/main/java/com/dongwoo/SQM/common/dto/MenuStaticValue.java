package com.dongwoo.SQM.common.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MenuStaticValue {
    public static List<List<SecurityUrlMatcherDTO>> menuList = new ArrayList<List<SecurityUrlMatcherDTO>>(); // 어플리케이션 기동 시 저장, 메뉴 정보가 추가/수정/삭제될 때 다시 저장
    public static List<List<SubMenuDTO>> subMenuList = new ArrayList<List<SubMenuDTO>>(); // 어플리케이션 기동 시 저장, 메뉴 정보가 추가/수정/삭제될 때 다시 저장
    public static List<List<SecurityUrlMatcherDTO>> adminMenuList = new ArrayList<List<SecurityUrlMatcherDTO>>(); // 어플리케이션 기동 시 저장, 메뉴 정보가 추가/수정/삭제될 때 다시 저장
    public static List<List<SubMenuDTO>> adminSubMenuList = new ArrayList<List<SubMenuDTO>>(); // 어플리케이션 기동 시 저장, 메뉴 정보가 추가/수정/삭제될 때 다시 저장
    public static List<List<SecurityUrlMatcherDTO>> userMenuList = new ArrayList<List<SecurityUrlMatcherDTO>>(); // 어플리케이션 기동 시 저장, 메뉴 정보가 추가/수정/삭제될 때 다시 저장
    public static List<List<SubMenuDTO>> userSubMenuList = new ArrayList<List<SubMenuDTO>>(); // 어플리케이션 기동 시 저장, 메뉴 정보가 추가/수정/삭제될 때 다시 저장
}