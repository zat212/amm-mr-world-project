package org.myassignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PackageItemId implements Serializable {

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "item_id")
    private Long itemId;
}
