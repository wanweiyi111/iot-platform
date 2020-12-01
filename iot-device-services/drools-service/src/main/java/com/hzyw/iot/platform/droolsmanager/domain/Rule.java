package com.hzyw.iot.platform.droolsmanager.domain;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="RULE_INFO_T")
@Data
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String ruleKey;
    @Column(nullable = false)
    private String ruleName;
    @Column(nullable = false)
    private String content;
    @Column(nullable = true, unique = true)
    private String version;
    @Column(nullable = true, unique = true)
    private Timestamp lastModifyTime;
    @Column(nullable = false)
    private Timestamp createTime;
    @Column(nullable = false)
    private Integer isenable;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}