package com.amrsamy.mep.axis.repository;

import com.amrsamy.mep.axis.model.CaseStudy;
import java.util.List;

public interface CaseStudyRepository {

    /** Newest first. */
    List<CaseStudy> findAll();
}
