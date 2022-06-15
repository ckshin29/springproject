package com.example.teamprojeect.service;

import com.example.teamprojeect.domain.dao.work.WorkDAO;
import com.example.teamprojeect.domain.dao.work.WorkFileDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkService {
    private WorkDAO workDAO;
    private WorkFileDAO workFileDAO;
}
