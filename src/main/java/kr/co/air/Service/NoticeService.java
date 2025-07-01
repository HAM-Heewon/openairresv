package kr.co.air.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.air.config.SftpConfigProperties;
import kr.co.air.dtos.DatalistDto;
import kr.co.air.mapper.NoticeMapper;
import kr.co.air.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
	
	private final NoticeMapper mapper;
	private final UserMapper usermapper;
	private final FtpService ftpService; // FTP 서비스 주입
	private final SftpConfigProperties sftpConfig;
	
	//공지사항 전체
	public List<DatalistDto> getAllNotice(){
		return mapper.findAllNotice();
	}
	
	//공지사항 입력
    @Transactional
    public void saveNotice(DatalistDto dto, MultipartFile mFile, Authentication auth) {
        
    	String writerName = "관리자";

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            writerName = userDetails.getUsername();
        }
        dto.setEname(writerName);

        Long empno = usermapper.findAdIdx(writerName);
        if (empno != null) {
            dto.setEmpno(empno);
        }
        
        // 파일 처리 로직 시작
        if (mFile != null && !mFile.isEmpty()) {
            try {
                // 1. 파일 정보 추출
                String originalFileName = mFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String savedFileName = UUID.randomUUID().toString() + fileExtension; // 중복 방지를 위한 UUID 파일명

                // 2. SFTP 서버로 직접 파일 전송 (메모리에서 바로 업로드)
                // SFTP 서버 내 저장될 경로 (baseDirectory + notice_files/년월일/파일)
                String relativeRemotePath = "notice_files/" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "/" + savedFileName;

                boolean uploadSuccess = ftpService.uploadFileFromStream(mFile.getInputStream(), relativeRemotePath);

                if (uploadSuccess) {
                    dto.setFileName(originalFileName);
                    dto.setFileSavename(savedFileName);
                    // DTO의 filePath는 SFTP 서버의 baseDirectory를 포함한 최종 경로로 저장
                    String fullPathForDb = sftpConfig.getBaseDirectory();
                    if (fullPathForDb == null || fullPathForDb.isEmpty()) {
                        fullPathForDb = "/";
                    } else if (!fullPathForDb.endsWith("/")) {
                        fullPathForDb += "/";
                    }
                    dto.setFilePath(fullPathForDb + relativeRemotePath); // DB에 저장될 전체 경로
                    dto.setFileSize(mFile.getSize());
                    //log.info("SFTP 서버에 파일 업로드 성공 및 DB 정보 설정 완료: {}", dto.getFilePath());
                } else {
                    //log.error("SFTP 파일 업로드 실패: {}", originalFileName);
                    dto.setFileName(null);
                    dto.setFileSavename(null);
                    dto.setFilePath(null);
                    dto.setFileSize(0L);
                }

            } catch (IOException e) {
                //log.error("파일 업로드 처리 중 IO 오류 발생: {}", e.getMessage(), e);
                dto.setFileName(null);
                dto.setFileSavename(null);
                dto.setFilePath(null);
                dto.setFileSize(0L);
            } catch (Exception e) {
                //log.error("SFTP 파일 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                dto.setFileName(null);
                dto.setFileSavename(null);
                dto.setFilePath(null);
                dto.setFileSize(0L);
            }
        } else {
            dto.setFileName(null);
            dto.setFileSavename(null);
            dto.setFilePath(null);
            dto.setFileSize(0L);
        }

        // 4. empno가 설정된 DTO를 DB에 저장
        mapper.insertNotice(dto);
    }
	
	//공지 상세
    @Transactional 
    public DatalistDto getNoticeById(int eno) {
        // 1. 조회수 1 증가
        mapper.incrementView(eno);
        // 2. 게시글 정보 조회 후 반환
        return mapper.findById(eno);
    }
    
    //공지 수정을 위한 상세 데이터 조회
    public DatalistDto getNoticeForUpdate(int eno) {
        return mapper.findById(eno);
    }
    
    //공지 수정
    @Transactional
    public void updateNotice(DatalistDto dto, MultipartFile mFile) {
    	 DatalistDto existingNotice = mapper.findById(dto.getEno());

         if (mFile != null && !mFile.isEmpty()) {
             try {
                 // 기존 파일이 있다면 SFTP 서버에서 삭제
                 if (existingNotice != null && existingNotice.getFilePath() != null && !existingNotice.getFilePath().isEmpty()) {
                     // filePath는 DB에 baseDirectory 포함된 전체 경로로 저장되어 있으므로, FtpService에서 상대 경로로 변환하여 삭제합니다.
                     boolean deleteSuccess = ftpService.deleteFile(existingNotice.getFilePath());
                     if (deleteSuccess) {
                         //log.info("기존 SFTP 파일 삭제 성공: {}", existingNotice.getFilePath());
                     } else {
                         //log.warn("기존 SFTP 파일 삭제 실패 또는 파일 없음: {}", existingNotice.getFilePath());
                     }
                 }

                 String originalFileName = mFile.getOriginalFilename();
                 String fileExtension = "";
                 if (originalFileName.contains(".")) {
                     fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                 }
                 String savedFileName = UUID.randomUUID().toString() + fileExtension;

                 String relativeRemotePath = "notice_files/" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "/" + savedFileName;

                 boolean uploadSuccess = ftpService.uploadFileFromStream(mFile.getInputStream(), relativeRemotePath);
                 if (uploadSuccess) {
                     dto.setFileName(originalFileName);
                     dto.setFileSavename(savedFileName);
                     String fullPathForDb = sftpConfig.getBaseDirectory();
                     if (fullPathForDb == null || fullPathForDb.isEmpty()) {
                         fullPathForDb = "/";
                     } else if (!fullPathForDb.endsWith("/")) {
                         fullPathForDb += "/";
                     }
                     dto.setFilePath(fullPathForDb + relativeRemotePath);
                     dto.setFileSize(mFile.getSize());
                     //log.info("파일 수정 시 새 파일 SFTP 업로드 성공 및 DB 정보 설정 완료: {}", dto.getFilePath());
                 } else {
                     //log.error("파일 수정 시 새 파일 SFTP 업로드 실패: {}", originalFileName);
                     // 업로드 실패 시 기존 파일 정보 유지 또는 null 처리
                     dto.setFileName(existingNotice.getFileName());
                     dto.setFileSavename(existingNotice.getFileSavename());
                     dto.setFilePath(existingNotice.getFilePath());
                     dto.setFileSize(existingNotice.getFileSize());
                 }

             } catch (IOException e) {
                 //log.error("파일 수정 처리 중 IO 오류 발생: {}", e.getMessage(), e);
                 dto.setFileName(existingNotice.getFileName());
                 dto.setFileSavename(existingNotice.getFileSavename());
                 dto.setFilePath(existingNotice.getFilePath());
                 dto.setFileSize(existingNotice.getFileSize());
             } catch (Exception e) {
                 //log.error("파일 수정 중 SFTP 처리 오류: {}", e.getMessage(), e);
                 dto.setFileName(existingNotice.getFileName());
                 dto.setFileSavename(existingNotice.getFileSavename());
                 dto.setFilePath(existingNotice.getFilePath());
                 dto.setFileSize(existingNotice.getFileSize());
             }
         } else {
             // 파일이 첨부되지 않은 경우, 기존 파일 정보를 유지
             // (만약 "파일 삭제" 체크박스 같은 기능이 있다면 여기서 처리)
             dto.setFileName(existingNotice.getFileName());
             dto.setFileSavename(existingNotice.getFileSavename());
             dto.setFilePath(existingNotice.getFilePath());
             dto.setFileSize(existingNotice.getFileSize());
         }
         mapper.updateNotice(dto);
     }
    
    //공지 삭제
    @Transactional
    public void deleteNoticesByIds(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            List<DatalistDto> noticesToDelete = mapper.findByNos(ids);
            for (DatalistDto notice : noticesToDelete) {
                if (notice.getFilePath() != null && !notice.getFilePath().isEmpty()) {
                    try {
                        // DB에 저장된 filePath는 baseDirectory 포함된 전체 경로이므로, FtpService에서 상대 경로로 변환하여 삭제
                        boolean deleteSuccess = ftpService.deleteFile(notice.getFilePath());
                        if (deleteSuccess) {
                            //log.info("SFTP 파일 삭제 성공: {}", notice.getFilePath());
                        } else {
                            //log.warn("SFTP 파일 삭제 실패 또는 파일 없음: {}", notice.getFilePath());
                        }
                    } catch (Exception e) {
                        //log.error("SFTP 파일 삭제 중 오류 발생 (ENO: {}, Path: {}): {}", notice.getEno(), notice.getFilePath(), e.getMessage(), e);
                    }
                }
            }
            mapper.deleteByIds(ids);
        }
    }
}
