package com.github.pengpan.request;

import lombok.Data;

@Data
public class CaptchaCollectRequest {

    private String image;

    private String code;
}
