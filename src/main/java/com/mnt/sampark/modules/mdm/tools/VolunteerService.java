package com.mnt.sampark.modules.mdm.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.modules.mdm.db.domain.Volunteer;
import com.mnt.sampark.modules.mdm.db.repository.VolunteerRepository;

@Component
public class VolunteerService {

    @Autowired
    VolunteerRepository volunteerRepository;

    public Volunteer findByMobile(String mobile) {
        return volunteerRepository.findByMobile(mobile);
    }

    public Volunteer save(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    public Volunteer findByMobileAndAssemblyConstituencyId(String mobile, String assemblyConstituencyId) {
        return volunteerRepository.findByMobileAndAssemblyConstituencyId(mobile, assemblyConstituencyId);
    }

    public Volunteer findById(Long id) {
        return volunteerRepository.findById(id).get();
    }

}
