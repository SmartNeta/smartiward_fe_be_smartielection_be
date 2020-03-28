package com.mnt.sampark.modules.mdm.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.modules.mdm.db.domain.AssemblyConstituency;
import com.mnt.sampark.modules.mdm.db.domain.Booth;
import com.mnt.sampark.modules.mdm.db.domain.District;
import com.mnt.sampark.modules.mdm.db.domain.ParliamentaryConstituency;
import com.mnt.sampark.modules.mdm.db.domain.StateAssembly;
import com.mnt.sampark.modules.mdm.db.domain.Ward;
import com.mnt.sampark.modules.mdm.db.repository.AssemblyConstituencyRepository;
import com.mnt.sampark.modules.mdm.db.repository.BoothRepository;
import com.mnt.sampark.modules.mdm.db.repository.DistrictRepository;
import com.mnt.sampark.modules.mdm.db.repository.ParliamentaryConstituencyRepository;
import com.mnt.sampark.modules.mdm.db.repository.StateAssemblyRepository;
import com.mnt.sampark.modules.mdm.db.repository.WardRepository;

@Component
public class HierarchyUploadService {

    @Autowired
    StateAssemblyRepository stateAssemblyRepository;

    @Autowired
    BoothRepository boothRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    AssemblyConstituencyRepository assemblyConstituencyRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    ParliamentaryConstituencyRepository parliamentaryConstituencyRepository;

    private static final DataFormatter formatter = new DataFormatter();

    public Map<String, Object> parse(File readableFile, Long stateAssemblyId) throws IOException {
        FileInputStream wbFile = new FileInputStream(readableFile);
        Workbook workbook = new XSSFWorkbook(wbFile);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> success = new ArrayList<String>();
        List<String> failed = new ArrayList<String>();
        for (int index = 1; index < sheet.getPhysicalNumberOfRows(); index++) {
            try {
                Row row = sheet.getRow(index);
                HierarchyPojo h = new HierarchyPojo(stateAssemblyId, row);
                if (Strings.isNotEmpty(h.cBname) && Strings.isNotEmpty(h.cWname) && Strings.isNotEmpty(h.cAname) && Strings.isNotEmpty(h.cPname) && Strings.isNotEmpty(h.cDname)) {
                    if (process(h)) {
                        success.add(index + " : Added successfully");
                    } else {
                        failed.add(index + " : Unable to create record");
                    }
                }
            } catch (Exception e) {
                failed.add(index + " : Unable to read row");
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }

    private boolean process(HierarchyPojo h) {
        Booth booth = null;
        try {
            booth = processBooth(h);
        } catch (Exception e) {
        }
        return booth != null;
    }

    private Booth processBooth(HierarchyPojo h) {
        Ward ward = processWard(h);
        Booth booth = boothRepository.customFind(ward.getId(), h.cBno);
        if (booth == null) {
            booth = new Booth();
        }
        booth.setName(h.cBname);
        booth.setNo(h.cBno);
        booth.setAddress(h.cBadd);
        booth.setWard(ward);
        booth = boothRepository.save(booth);
        return booth;
    }

    private Ward processWard(HierarchyPojo h) {
        AssemblyConstituency assemblyConstituency = processAssemblyConstituency(h);
        Ward ward = wardRepository.customFind(assemblyConstituency.getId(), h.cWno);
        if (ward == null) {
            ward = new Ward();
        }
        ward.setName(h.cWname);
        ward.setNo(h.cWno);
        ward.setAreaAddress(h.cWadd);
        ward.setAssemblyConstituency(assemblyConstituency);
        ward = wardRepository.save(ward);
        return ward;
    }

    private AssemblyConstituency processAssemblyConstituency(HierarchyPojo h) {
        ParliamentaryConstituency parliamentaryConstituency = processParliamentaryConstituency(h);
        AssemblyConstituency assemblyConstituency = assemblyConstituencyRepository.customFind(parliamentaryConstituency.getId(), h.cAno);
        if (assemblyConstituency == null) {
            assemblyConstituency = new AssemblyConstituency();
        }
        assemblyConstituency.setName(h.cAname);
        assemblyConstituency.setNo(h.cAno);
        assemblyConstituency.setParliamentaryConstituency(parliamentaryConstituency);
        assemblyConstituency = assemblyConstituencyRepository.save(assemblyConstituency);
        return assemblyConstituency;
    }

    private ParliamentaryConstituency processParliamentaryConstituency(HierarchyPojo h) {
        District district = processDistrict(h);
        ParliamentaryConstituency parliamentaryConstituency = parliamentaryConstituencyRepository.customFind(district.getId(), h.cPno);
        if (parliamentaryConstituency == null) {
            parliamentaryConstituency = new ParliamentaryConstituency();
        }
        parliamentaryConstituency.setNo(h.cPno);
        parliamentaryConstituency.setName(h.cPname);
        parliamentaryConstituency.setDistrict(district);
        parliamentaryConstituency = parliamentaryConstituencyRepository.save(parliamentaryConstituency);
        return parliamentaryConstituency;
    }

    private District processDistrict(HierarchyPojo h) {
        StateAssembly stateAssembly = processStateAssembly(h);
        District district = districtRepository.customFind(stateAssembly.getId(), h.cDno);
        if (district == null) {
            district = new District();
        }
        district.setStateAssembly(stateAssembly);
        district.setName(h.cDname);
        district.setNo(h.cDno);
        district = districtRepository.save(district);
        return district;
    }

    private StateAssembly processStateAssembly(HierarchyPojo h) {
        return stateAssemblyRepository.findById(h.stateAssemblyId).get();
    }

    private class HierarchyPojo {

        String cDno;
        String cDname;
        String cPno;
        String cPname;
        String cAno;
        String cAname;
        String cWno;
        String cWname;
        String cWadd;
        String cBno;
        String cBname;
        String cBadd;
        Long stateAssemblyId;

        public HierarchyPojo(Long stateAssemblyId, Row row) {
            this.stateAssemblyId = stateAssemblyId;
            this.cDno = formatter.formatCellValue(row.getCell(0));
            this.cDname = formatter.formatCellValue(row.getCell(1));
            this.cPno = formatter.formatCellValue(row.getCell(2));
            this.cPname = formatter.formatCellValue(row.getCell(3));
            this.cAno = formatter.formatCellValue(row.getCell(4));
            this.cAname = formatter.formatCellValue(row.getCell(5));
            this.cWno = formatter.formatCellValue(row.getCell(6));
            this.cWname = formatter.formatCellValue(row.getCell(7));
            this.cWadd = formatter.formatCellValue(row.getCell(8));
            this.cBno = formatter.formatCellValue(row.getCell(9));
            this.cBname = formatter.formatCellValue(row.getCell(10));
            this.cBadd = formatter.formatCellValue(row.getCell(11));
        }
    }

}
