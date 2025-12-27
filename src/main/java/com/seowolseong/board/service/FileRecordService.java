package com.seowolseong.board.service;

import com.seowolseong.board.domain.FileStatus;
import com.seowolseong.board.domain.PostFile;
import com.seowolseong.board.repository.PostFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 파일 메타데이터 상태 관리 서비스
 *
 * - 파일 업로드 흐름에서 상태(PENDING/READY/FAILED)를 독립 트랜잭션으로 관리한다.
 */
@Service
public class FileRecordService {

    private final PostFileRepository repo;

    public FileRecordService(PostFileRepository repo) {
        this.repo = repo;
    }

    /**
     * 파일 메타데이터 생성 (PENDING)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PostFile createPending(PostFile pf) {
        return repo.saveAndFlush(pf);
    }

    /**
     * 파일 업로드 완료 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PostFile markReady(Long id) {
        PostFile pf = repo.findById(id).orElseThrow();
        pf.setStatus(FileStatus.READY);
        pf.setErrorMessage(null);
        return pf;
    }

    /**
     * 파일 업로드 실패 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PostFile markFailed(Long id, String msg) {
        PostFile pf = repo.findById(id).orElseThrow();
        pf.setStatus(FileStatus.FAILED);
        pf.setErrorMessage(msg);
        return pf;
    }
}
