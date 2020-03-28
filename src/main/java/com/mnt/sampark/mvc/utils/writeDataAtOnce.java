package com.mnt.sampark.mvc.utils;

import com.mnt.sampark.modules.mdm.db.domain.Complaint;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class writeDataAtOnce {

    public static File writeDataAtOnce1(String filePath, List<Complaint> complaints) {

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            String heder[] = {"Sr.No", "Name", "Voter Id", "Incident Id", "Sub Department", "Complaint Text", "User", "Action", "Comments", "Status", "Source", "date_handed_over", "Tentative date of Completion", "Latitude", "Longitude", "Citizen Mobile"};
            data.add(heder);
            for (int i = 0; i < complaints.size(); i++) {
                Complaint c = complaints.get(i);
                String a[] = {"" + c.getId(), c.getName(), c.getCitizen().getVoterId(), c.getIncidentId(), c.getSubDepartment().getName(), c.getComplaint(), (c.getUser() == null ? "N/A" : c.getUser().getFirstName()), c.getAction(), c.getCommentsFromDepartment(), c.getStatus(), c.getCompliantSource(), "" + c.getDateHandedOverToResponsibleDepartment(), "" + c.getTentativeDateOfCompletion(), c.getLatitude(), c.getLongitude(), (Objects.nonNull(c.getCitizen().getMobile()) && !(c.getCitizen().getMobile() + "").equalsIgnoreCase("null") ? c.getCitizen().getMobile() + "" : "")};
                data.add(a);
            }
            writer.writeAll(data);
            // closing writer connection
            writer.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
}
