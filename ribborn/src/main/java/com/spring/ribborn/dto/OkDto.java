package com.spring.ribborn.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OkDto {
    private Boolean ok;

    public static OkDto valueOf(String value){

        OkDto okDto = new OkDto();
        if ( value.equals("true")){ okDto.ok = true; }
        else if ( value.equals("false") ){ okDto.ok = false; }

        return okDto;
    }
}
