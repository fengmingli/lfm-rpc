package com.lifengming.rpc.common.constans;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lifengming
 * @date 2021.07.25
 */
@Getter
@AllArgsConstructor
public enum RpcStatusEnum {
    /**
     * SUCCESS
     */
    SUCCESS(200, "SUCCESS"),
    /**
     * ERROR
     */
    ERROR(500, "ERROR"),
    /**
     * NOT FOUND
     */
    NOT_FOUND(404, "NOT FOUND");

    private final int code;

    private final String desc;
}
