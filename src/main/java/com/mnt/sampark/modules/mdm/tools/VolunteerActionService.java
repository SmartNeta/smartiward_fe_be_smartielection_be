package com.mnt.sampark.modules.mdm.tools;

import com.mnt.sampark.modules.mdm.db.domain.Segmentations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.modules.mdm.db.domain.StateAssembly;
import com.mnt.sampark.modules.mdm.db.domain.VolunteerAction;
import com.mnt.sampark.modules.mdm.db.repository.SegmentationsRepository;
import com.mnt.sampark.modules.mdm.db.repository.VolunteerActionRepository;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VolunteerActionService {

    private static final List<String> actions = new ArrayList<String>();
    private static final List<String> segmentations = new ArrayList<String>();

    static {
        actions.add("mobile");
        actions.add("segmentation");
        actions.add("partypreference");
        actions.add("survey");
        actions.add("markasVoted");
        actions.add("print");
        actions.add("share");
        actions.add("status");
        actions.add("registeredComplaints");
        actions.add("otherInformation");
    }

    static {
        segmentations.add("A+");
        segmentations.add("A");
        segmentations.add("B");
        segmentations.add("C");
    }

    @Autowired
    VolunteerActionRepository volunteerActionRepository;

    @Autowired
    SegmentationsRepository segmentationsRepository;

    public List<VolunteerAction> getActions(Long stateId) {
        StateAssembly stateAssembly = new StateAssembly();
        stateAssembly.setId(stateId);
        List<VolunteerAction> volunteerActions = new ArrayList<VolunteerAction>();
        for (String action : actions) {
            VolunteerAction volunteerAction = volunteerActionRepository.findByStateAssemblyIdAndAction(stateId, action);
            if (volunteerAction == null) {
                volunteerAction = new VolunteerAction();
                volunteerAction.setStateAssembly(stateAssembly);
                volunteerAction.setAction(action);
                volunteerAction.setLabel(action);
                volunteerAction.setSequence(actions.indexOf(action));
                volunteerActionRepository.save(volunteerAction);
            }
            volunteerActions.add(volunteerAction);
        }
        return volunteerActions;
    }

    public List<Segmentations> getSegmentations(Long stateId) {
        StateAssembly stateAssembly = new StateAssembly();
        stateAssembly.setId(stateId);
        List<Segmentations> segmentationsList = new ArrayList<Segmentations>();
        segmentations.stream().map((segmentation) -> {
            Segmentations segmentationEntity = segmentationsRepository.findByStateAssemblyIdAndSegmentation(stateId, segmentation);
            if (segmentationEntity == null) {
                segmentationEntity = new Segmentations();
                segmentationEntity.setStateAssembly(stateAssembly);
                segmentationEntity.setSegmentation(segmentation);
                segmentationEntity.setLabel(segmentation);
                segmentationEntity.setSequence(segmentations.indexOf(segmentation));
                segmentationsRepository.save(segmentationEntity);
            }
            return segmentationEntity;
        }).forEachOrdered((segmentationEntity) -> {
            segmentationsList.add(segmentationEntity);
        });
        return segmentationsList;
    }

    public List<VolunteerAction> findAll(Long stateId) {
        return volunteerActionRepository.findAllByStateAssemblyIdAndVisibilityOrderBySequenceAsc(stateId, true);
    }

    public List<Segmentations> findAllSegmentations(Long stateId) {
        return segmentationsRepository.findAllByStateAssemblyIdAndVisibilityOrderBySequenceAsc(stateId, true);
    }

    public List<Map<String, Object>> getSegmentationLabelsByState(Long stateId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Object[]> resultArray = segmentationsRepository.findAllLabelsAndSegmentationByStateAssemblyIdAndVisibilityOrderBySequenceAsc(stateId);
        for (Object[] action : resultArray) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", action[0] + "");
            map.put("segmentation", action[1] + "");
            resultList.add(map);
        }
        return resultList;
    }

    public List<Map<String, Object>> findAllSegmentationsList(Long stateId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Object[]> resultArray = segmentationsRepository.findAllLabelsAndSegmentationByStateAssemblyIdAndVisibilityOrderBySequenceAsc(stateId);
        resultArray.forEach((action) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("label", action[0] + "");
            map.put("segmentation", action[1] + "");
            resultList.add(map);
        });
        return resultList;
    }

}
