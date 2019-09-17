package com.lfm.rpc.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lifengming
 * @since 16.09.2019
 */
@Setter
@Getter
@ToString
public class User implements Serializable{
    private static final long serialVersionUID = -8044335891204727911L;

    private String name;
}
