package ru.koshkin.communication;

public interface ResponseDetailsOptions {
    String ALLOWED="allowed";
    String DECLINED="i_need_be_green";
    String BOOKED="already_booked";
    String RED="i_am_red";
    String JUST_GREEN="i_just_became_green";
    String RED_BUT_CAN_CHANGE="i_am_red_but_want_change";
    String CONFIRMED = "confirmed";
}
