package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.SampleFileDTO;
import com.dongwoo.SQM.siteMgr.service.SampleFileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SampleFileController {

    private final SampleFileService sampleFileService;
    private final String fileBasePath ="./upload";

    @GetMapping("/admin/siteMgr/samplefile")
    public String findAll(@RequestParam(value="sLang",defaultValue = "KOR") String sLang,Model model){
        //if(sLang == null) sLang="KOR";
        log.info("test2dfasdfasdfasdfasdf");
        List<HashMap> korPlantList = sampleFileService.plantList(sLang);
        //List<HashMap> engPlantList = sampleFileService.plantList("ENG");
        //List<HashMap> jpnPlantList = sampleFileService.plantList("JPN");
        //List<HashMap> chnPlantList = sampleFileService.plantList("CHN");
        //기초코드 리스트
        model.addAttribute("korPlantListArray", korPlantList);
        log.info("test"+korPlantList);
        //검색,모달 업무구분 리스트
//        for(int i = 0; i<korPlantList.size();i++) {
//            String baseCode = korPlantList.get(i).get("BASE_CODE").toString();
//            List<?> orgList = sampleFileService.getPlantFileInfo(baseCode);
//            for()
//        }
//        String test = "1200";
//        model.addAttribute("PLANT_ORGINFO","1200:5|9.3_SVHC 확인서_241종.xlsx|./uploads/SAMPLEFILE/KOR/PLANT/1200/1/|24,29|9.3_SVHC 확인서_2412종.xlsx|./uploads/SAMPLEFILE/KOR/PLANT/1200/1/|24+");

        //model.addAttribute("engPlantList",engPlantList);
        //모달 그룹코드 리스트
        //model.addAttribute("jpnPlantList",jpnPlantList);

        //model.addAttribute("chnPlantList",chnPlantList);

        model.addAttribute("Language",sLang);

        //plant 부터 깔자
        //List<HashMap> korPlantList = sampleFileService.plantList("KOR");
        //model.addAttribute("chnPlantList",chnPlantList);

        List<SampleFileDTO> plantFileList = sampleFileService.getPlantFileInfo(sLang);
        model.addAttribute("plantFileList",plantFileList);

        return "sampleFile/list";
    }

    //plant 리스트 불러오기
    @GetMapping("/admin/siteMgmt/getPlant")
    public List<HashMap> plantList(){

        List<HashMap> splantList = new ArrayList<>();
        //splantList = sampleFileService.plantList();

        return splantList;
    }

    //plant 파일 리스트 가져오기
    @GetMapping("/admin/siteMgmt/getFileList")
    public @ResponseBody List<SampleFileDTO> getPlantFileList(@RequestParam("sLang") String sLang){
        log.info("test666666666666666666666666666666666");
        if(sLang == null) sLang="KOR";
        log.info("test7777777777777777777777777");
        List<SampleFileDTO> plantFileList = sampleFileService.getPlantFileInfo(sLang);


        return plantFileList;
    }

    @GetMapping("/admin/siteMgmt/getOtherFileList")
    public @ResponseBody List<SampleFileDTO> getOtherFileList(@RequestParam("sLang") String sLang) {
        List<SampleFileDTO> otherFileList = sampleFileService.getOtherFileInfo(sLang);

        return otherFileList;
    }

    @GetMapping("/admin/siteMgmt/download_bak")
    public ResponseEntity<Resource> downloadFile_bak(@RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName){
        try {
            Path file = Paths.get(filePath).resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() && resource.isReadable()){
                String encodedFileName = URLEncoder.encode(resource.getFilename(),"UTF-8").replace("+","%20");
                String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition)
                        .body(resource);

            }else{
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                return ResponseEntity.notFound().build();
            }

        }catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/admin/siteMgmt/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("baseCode") String baseCode
                                                , @RequestParam("fileNum") String fileNum
                                                , @RequestParam("sLang") String sLang
                                                , @RequestParam("GUBN") String GUBN){
        try {

            List<SampleFileDTO> fileInfo = sampleFileService.getFileInfo(baseCode,fileNum,sLang,GUBN);
            String filePath = "";
            String fileName ="";

            if(!fileInfo.isEmpty()) {
                SampleFileDTO filedto = fileInfo.get(0);
                filePath = filedto.getFILE_PATH();
                fileName = filedto.getFILE_NAME();
            }

            Path file = Paths.get(filePath).resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() && resource.isReadable()){
                String encodedFileName = URLEncoder.encode(resource.getFilename(),"UTF-8").replace("+","%20");
                String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition)
                        .body(resource);

            }else{
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                return ResponseEntity.notFound().build();
            }



        }catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    //@PostMapping("/admin/siteMgmt/upload")
    @RequestMapping("/admin/siteMgmt/upload")
    //public String uploadFiles(Model model, HttpServletRequest request
    public ResponseEntity<?> uploadFiles(Model model, HttpServletRequest request
                             ,@RequestParam(value="files",required = false)MultipartFile[] files){
        List<SampleFileDTO> samplefileDeleteList = new ArrayList<>();//삭제
        List<SampleFileDTO> samplefileUpdateList = new ArrayList<>();//업데이트
        List<SampleFileDTO> sampleFileDTOList = new ArrayList<>();//insert
        //List<String> fileNumList = new ArrayList<>();

        String sLang = GetParam(request,"FILE_LANG","");
        String[] sCase =  GetParam(request,"FILE_CASE","").split(",");
        String[] sGubn = GetParam(request,"FILE_GUBN","").split(",");
        String[] sNum = GetParam(request,"FILE_NUM","").split(",");
        String[] sStatus = GetParam(request,"FILE_STATUS","").split(",");

        log.info("=======length : "+files.length +"   언어:  "+sLang);

        //MultipartFile files1 = ((StandardMultipartHttpServletRequest) request).getFile("PLANT_1200_Files_FILE_file_input");

        for(int i =0; i<files.length; i++){
            SampleFileDTO sampleFileDTO = new SampleFileDTO();
            MultipartFile file = files[i];
            String sUrl = "/SAMPLEFILE/"+sLang+"/";
            if(!file.isEmpty()){
                //업데이트,  insert
                try{
                    sUrl += sCase[i] +"/"+ sGubn[i];       //\SAMPLFILE\"FILE_LANG"\"FILE_CASE"\"FILE_GUBUN"
                    if(!sGubn[i].isEmpty()) sUrl += "/";
                    sUrl += sNum[i] +"/";

                    String fileName = file.getOriginalFilename();
                    Path path = Paths.get("./uploads"+sUrl+fileName);
                    Files.createDirectories((path.getParent()));
                    file.transferTo((path));

                    log.info("lang : "+sLang);
                    log.info("sCase[i] : "+sCase[i]);
                    //log.info("sCase[i] : "+status[i]);

                    //dto 저장
                    sampleFileDTO.setFILE_LANG(sLang);
                    sampleFileDTO.setFILE_CASE(sCase[i]);
                    sampleFileDTO.setFILE_GUBN(sGubn[i]);
                    sampleFileDTO.setFILE_NUM(sNum[i]);
                    sampleFileDTO.setFILE_NAME(fileName);
                    sampleFileDTO.setFILE_PATH("./uploads"+sUrl);
                    log.info("status :" + sStatus[i]);
                    //insert-sampleFileDTOList update- fileUpdateList delete- fileDeleteList
                    if(sStatus[i].equals("UPDATE")) {
                        log.info("update :" + sampleFileDTO);
                        samplefileUpdateList.add(sampleFileDTO);
                    }else if(sStatus[i].equals("INSERT")) {
                        log.info("INSERT :" + sampleFileDTO);
                        sampleFileDTOList.add(sampleFileDTO);
                    }

                    //sampleFileDTOList.add(sampleFileDTO);

                }catch (IOException e){
                    e.printStackTrace();
                    model.addAttribute("message","파일업로드중 오류 : " + file.getOriginalFilename());
                    //return  "/sampleFile/list";
                    return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());

                }
            }
            else{
                log.info("status :" + sStatus[i]);
                // 파일만 삭제하는 경우
                if(sStatus[i].equals("DELETE")){
                    //dto 저장
                    sampleFileDTO.setFILE_LANG(sLang);
                    sampleFileDTO.setFILE_CASE(sCase[i]);
                    sampleFileDTO.setFILE_GUBN(sGubn[i]);
                    sampleFileDTO.setFILE_NUM(sNum[i]);
                    //sampleFileDTO.setFILE_NAME(fileName);
                    //sampleFileDTO.setFILE_PATH("./uploads"+sUrl);
                    samplefileDeleteList.add(sampleFileDTO);
                }
            }
        }
        log.info("test121212222222222222");
        //db저장
        sampleFileService.delete(samplefileDeleteList);
        log.info("test3333333333");
        sampleFileService.update(samplefileUpdateList);
        log.info("test4444444444444");
        sampleFileService.upload(sampleFileDTOList);

        log.info("test4455555555555555555");
        //templates/sampleFile/list.htm
        //return "redirect:/admin/siteMgr/samplefile?sLang="+sLang;
        return ResponseEntity.ok("OK");
    }

    @RequestMapping("/admin/siteMgmt/deleteOrgFile")
    public ResponseEntity<?> deleteOrgFile(Model model, HttpServletRequest request){
        int idx = Integer.parseInt(GetParam(request, "DEL_IDX","0"));
        sampleFileService.deleteOrgFile(idx);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/admin/siteMgmt/upload_bak")
    public String uploadFiles_bak(@RequestParam("files")MultipartFile[] files,
                              @RequestParam("FILE_LANG") String sLang,
                              @RequestParam("FILE_CASE") String[] sCase,
                              @RequestParam("FILE_GUBN") String[] sGubn,
                              @RequestParam("FILE_NUM") String[] sNum,
                              @RequestParam("FILE_STATUS") String[] sStatus,
                              Model model){
        List<SampleFileDTO> samplefileDeleteList = new ArrayList<>();//삭제
        List<SampleFileDTO> samplefileUpdateList = new ArrayList<>();//업데이트
        List<SampleFileDTO> sampleFileDTOList = new ArrayList<>();//insert
        //List<String> fileNumList = new ArrayList<>();
        log.info("=======length : "+files.length +"   언어:  "+sLang);

        for(int i =0; i<files.length; i++){
            SampleFileDTO sampleFileDTO = new SampleFileDTO();
            MultipartFile file = files[i];
            String sUrl = "/SAMPLEFILE/"+sLang+"/";
            if(!file.isEmpty()){
                //업데이트,  insert
                try{
                    sUrl += sCase[i] +"/"+ sGubn[i];       //\SAMPLFILE\"FILE_LANG"\"FILE_CASE"\"FILE_GUBUN"
                    if(!sGubn[i].isEmpty()) sUrl += "/";
                    sUrl += sNum[i] +"/";

                    String fileName = file.getOriginalFilename();
                    Path path = Paths.get("./uploads"+sUrl+fileName);
                    Files.createDirectories((path.getParent()));
                    file.transferTo((path));

                    log.info("lang : "+sLang);
                    log.info("sCase[i] : "+sCase[i]);
                    //log.info("sCase[i] : "+status[i]);

                    //dto 저장
                    sampleFileDTO.setFILE_LANG(sLang);
                    sampleFileDTO.setFILE_CASE(sCase[i]);
                    sampleFileDTO.setFILE_GUBN(sGubn[i]);
                    sampleFileDTO.setFILE_NUM(sNum[i]);
                    sampleFileDTO.setFILE_NAME(fileName);
                    sampleFileDTO.setFILE_PATH("./uploads"+sUrl);
                    log.info("status :" + sStatus[i]);
                    //insert-sampleFileDTOList update- fileUpdateList delete- fileDeleteList
                    if(sStatus[i].equals("UPDATE")) {
                        log.info("update :" + sampleFileDTO);
                        samplefileUpdateList.add(sampleFileDTO);
                    }else if(sStatus[i].equals("INSERT")) {
                        log.info("INSERT :" + sampleFileDTO);
                        sampleFileDTOList.add(sampleFileDTO);
                    }

                    //sampleFileDTOList.add(sampleFileDTO);

                }catch (IOException e){
                    e.printStackTrace();
                    model.addAttribute("message","파일업로드중 오류 : " + file.getOriginalFilename());
                    return  "/sampleFile/list";

                }
            }
            else{
                log.info("status :" + sStatus[i]);
                // 파일만 삭제하는 경우
                if(sStatus[i].equals("DELETE")){
                    //dto 저장
                    sampleFileDTO.setFILE_LANG(sLang);
                    sampleFileDTO.setFILE_CASE(sCase[i]);
                    sampleFileDTO.setFILE_GUBN(sGubn[i]);
                    sampleFileDTO.setFILE_NUM(sNum[i]);
                    //sampleFileDTO.setFILE_NAME(fileName);
                    //sampleFileDTO.setFILE_PATH("./uploads"+sUrl);
                    samplefileDeleteList.add(sampleFileDTO);
                }
            }
        }
        log.info("test121212222222222222");
        //db저장
        sampleFileService.delete(samplefileDeleteList);
        log.info("test3333333333");
        sampleFileService.update(samplefileUpdateList);
        log.info("test4444444444444");
        sampleFileService.upload(sampleFileDTOList);

        log.info("test4455555555555555555");
        //templates/sampleFile/list.htm
        return "redirect:/admin/siteMgr/samplefile?sLang="+sLang;
    }

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }






}
