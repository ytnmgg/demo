package com.hy.project.demo.util;

import com.hy.project.demo.model.sequence.SequenceNameEnum;
import com.hy.project.demo.repository.SequenceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2022/09/20
 */
@Component
public class IdGenerator {

    @Autowired
    SequenceRepository sequenceRepository;

    public String generateId(SequenceNameEnum sequenceNameEnum) {
        StringBuilder sb = new StringBuilder();
        sb.append(sequenceNameEnum.getCode());
        sb.append(DateUtil.formatToday());

        String sequence = String.valueOf(sequenceRepository.nextValue(sequenceNameEnum.getName()));
        String subStr = StringUtils.substring(sequence, -6);
        sb.append(StringUtils.leftPad(subStr, 6, '0'));
        return sb.toString();
    }
}
