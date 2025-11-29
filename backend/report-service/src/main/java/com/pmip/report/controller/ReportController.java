package com.pmip.report.controller;

import com.pmip.report.model.Project;
import com.pmip.report.model.Task;
import com.pmip.report.repo.ProjectRepository;
import com.pmip.report.repo.TaskRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;

    @GetMapping("/powerbi/projects")
    public List<Map<String, Object>> powerBiProjects() {
        List<Project> projects = projectRepository.findAll();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Project p : projects) {
            long tasks = taskRepository.findByProjectId(p.getId()).size();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("projectId", p.getId());
            row.put("title", p.getTitle());
            row.put("status", p.getStatus());
            row.put("progress", p.getProgress());
            row.put("tasksCount", tasks);
            row.put("startDate", p.getStartDate());
            row.put("endDate", p.getEndDate());
            list.add(row);
        }
        return list;
    }

    @GetMapping("/project/{id}/json")
    public ResponseEntity<?> projectJson(@PathVariable Long id) {
        Optional<Project> opt = projectRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        List<Task> tasks = taskRepository.findByProjectId(id);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("project", opt.get());
        payload.put("tasks", tasks);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/project/{id}/csv")
    public ResponseEntity<byte[]> projectCsv(@PathVariable Long id) {
        Optional<Project> opt = projectRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        List<Task> tasks = taskRepository.findByProjectId(id);
        StringBuilder sb = new StringBuilder();
        sb.append("TaskId,Title,Status,Priority,EstimatedDuration,DueDate\n");
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        for (Task t : tasks) {
            sb.append(t.getId()).append(',')
              .append(escape(t.getTitle())).append(',')
              .append(nullToEmpty(t.getStatus())).append(',')
              .append(nullToEmpty(t.getPriority())).append(',')
              .append(t.getEstimatedDuration()==null?"":t.getEstimatedDuration()).append(',')
              .append(t.getDueDate()==null?"":df.format(t.getDueDate()))
              .append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_"+id+".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @GetMapping("/project/{id}/excel")
    public ResponseEntity<byte[]> projectExcel(@PathVariable Long id) throws Exception {
        Optional<Project> opt = projectRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        List<Task> tasks = taskRepository.findByProjectId(id);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Tasks");
        int r=0;
        Row header = sheet.createRow(r++);
        header.createCell(0).setCellValue("TaskId");
        header.createCell(1).setCellValue("Title");
        header.createCell(2).setCellValue("Status");
        header.createCell(3).setCellValue("Priority");
        header.createCell(4).setCellValue("EstimatedDuration");
        header.createCell(5).setCellValue("DueDate");
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        for (Task t : tasks) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(nullToEmpty(t.getTitle()));
            row.createCell(2).setCellValue(nullToEmpty(t.getStatus()));
            row.createCell(3).setCellValue(nullToEmpty(t.getPriority()));
            row.createCell(4).setCellValue(t.getEstimatedDuration()==null?0:t.getEstimatedDuration());
            row.createCell(5).setCellValue(t.getDueDate()==null?"":df.format(t.getDueDate()));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos); wb.close();
        byte[] bytes = bos.toByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_"+id+".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    private String escape(String v){ return v==null?"":('"'+v.replace("\"","'")+'"'); }
    private String nullToEmpty(String v){ return v==null?"":v; }
}
