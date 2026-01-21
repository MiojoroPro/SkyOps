package com.aero.ops.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PrixClasseId implements Serializable {
    private Long volDetail;
    private Long classeSiege;
}
