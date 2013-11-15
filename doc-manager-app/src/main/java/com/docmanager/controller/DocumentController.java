package com.docmanager.controller;

import com.docmanager.api.DocumentDao;
import com.docmanager.api.DocumentTypeDao;
import com.docmanager.model.Document;
import com.docmanager.model.DocumentType;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.util.List;

@Controller
@RequestMapping(value = "/documents")
public class DocumentController {

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private DocumentTypeDao documentTypeDao;

    @RequestMapping(value = "/list")
    public String list(ModelMap model) {
        List<Document> documents = documentDao.findAll();
        model.addAttribute("list", documents);
        return "document/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(ModelMap model) {
        List<DocumentType> documentTypes = documentTypeDao.findAll();
        model.addAttribute("types", documentTypes);
        model.addAttribute("command", new Document());
        return "document/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(@ModelAttribute("document") @Valid Document document, @RequestParam("file") MultipartFile file) {
        try {
//            DocumentType documentType1 = documentTypeDao.find(documentType.getId());
//            document.setDocumentType(documentType1);
            documentDao.insert(document, file);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "redirect:/document/list";
    }

    public String remove(@PathVariable("documentId") int documentId) {
        Document document = documentDao.find(documentId);
        if (document != null) {
            documentDao.delete(document);
        }
        return "redirect:/documents/list";
    }

    @RequestMapping(value = "/get/{documentId}", method = RequestMethod.GET)
    public void get(HttpServletResponse response, @PathVariable("documentId") int documentId) {
        try {
            Document document = documentDao.find(documentId);
            response.setHeader("Content-Disposition", "inline;filename=\"" + document.getName()+ "\"");
            OutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            IOUtils.copy(document.getContent().getBinaryStream(), out);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}