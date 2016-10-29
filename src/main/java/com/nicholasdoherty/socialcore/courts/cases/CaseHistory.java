package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Created by john on 1/9/15.
 */
public class CaseHistory implements ConfigurationSerializable{
    List<HistoryEntry> history;

    public CaseHistory(List<HistoryEntry> history) {
        this.history = history;
    }

    public CaseHistory() {
        history = new ArrayList<>();
    }

    public void record(Case caze, CaseStatus caseStatus, String name) {
        Date date = new Date();
        HistoryEntry historyEntry = new HistoryEntry(date.getTime(),caseStatus,name);
        Courts.getCourts().getSqlSaveManager().addCaseHistoryEntry(caze,historyEntry);
        history.add(historyEntry);
    }
    public HistoryEntry getProcessingEntry() {
        for (HistoryEntry historyEntry : history) {
            if (historyEntry.getCaseStatus() == CaseStatus.PROCESSED || historyEntry.getCaseStatus() == CaseStatus.THROWN_OUT) {
                return historyEntry;
            }
        }
        return null;
    }
    public String getResolverName() {
        for (HistoryEntry historyEntry : history) {
            if (historyEntry.getCaseStatus() == CaseStatus.RESOLVED) {
                String resp = historyEntry.getResponsible();
                return resp;
            }
        }
        return null;
    }
    public Optional<HistoryEntry> getSubmitterEntry() {
        return history.stream().filter(historyEntry -> historyEntry.getCaseStatus() == CaseStatus.UNPROCESSED).findFirst();
    }
    public Optional<HistoryEntry> getResolveEntry() {
        return history.stream().filter(historyEntry -> historyEntry.getCaseStatus() == CaseStatus.RESOLVED).findFirst();
    }
    public Citizen getSubmitter() {
        for (HistoryEntry historyEntry : history) {
            if (historyEntry.getCaseStatus() == CaseStatus.UNPROCESSED) {
                String resp = historyEntry.getResponsible();
                if (resp != null) {
                    Citizen respC = Courts.getCourts().getCitizenManager().getCitizen(resp);
                    if (respC != null) {
                        return respC;
                    }
                }
            }
        }
        return null;
    }
    public CaseHistory(Map<String, Object> map) {
        this.history = new ArrayList<>((List<HistoryEntry>)map.get("history"));
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("history",history);
        return map;
    }

    public static class HistoryEntry implements ConfigurationSerializable{
        private long date;
        private CaseStatus caseStatus;
        private String responsible;

        public HistoryEntry(long date, CaseStatus caseStatus, String responsible) {
            this.date = date;
            this.caseStatus = caseStatus;
            this.responsible = responsible;
        }

        public long getDate() {
            return date;
        }

        public CaseStatus getCaseStatus() {
            return caseStatus;
        }

        public String getResponsible() {
            return responsible;
        }
        public HistoryEntry(Map<String,Object> map) {
            this.date = (long) map.get("date");
            this.caseStatus = CaseStatus.valueOf((String) map.get("case-status"));
            this.responsible = (String) map.get("responsible");
        }
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("date",date);
            map.put("case-status",caseStatus.toString());
            map.put("responsible",responsible);
            return map;
        }
    }
}
