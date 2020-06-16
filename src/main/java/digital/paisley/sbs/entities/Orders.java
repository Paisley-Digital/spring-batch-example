package digital.paisley.sbs.entities;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Orders implements java.io.Serializable {

    private static final long serialVersionUID = -4497997807305980954L;

    public Long id;

    @NotBlank
    @Length(max = 20)
    public String orderId;

    public Date orderDate;

    public String productName;

}