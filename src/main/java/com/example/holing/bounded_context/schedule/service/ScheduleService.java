package com.example.holing.bounded_context.schedule.service;

import com.example.holing.base.exception.GlobalException;
import com.example.holing.bounded_context.schedule.dto.ScheduleRequestDto;
import com.example.holing.bounded_context.schedule.dto.ScheduleResponseDto;
import com.example.holing.bounded_context.schedule.entity.Schedule;
import com.example.holing.bounded_context.schedule.exception.ScheduleExceptionCode;
import com.example.holing.bounded_context.schedule.repository.ScheduleRepository;
import com.example.holing.bounded_context.user.entity.User;
import com.example.holing.bounded_context.user.exception.UserExceptionCode;
import com.example.holing.bounded_context.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 일정 등록
     *
     * @param scheduleRequestDto
     */
    public ScheduleResponseDto create(Long userId, ScheduleRequestDto scheduleRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserExceptionCode.USER_NOT_FOUND));

        validate(scheduleRequestDto.startAt(), scheduleRequestDto.finishAt());
        Schedule schedule = scheduleRequestDto.toEntity();

        schedule.setUser(user);
        scheduleRepository.save(schedule);

        return ScheduleResponseDto.fromEntity(schedule);
    }

    /**
     * 오늘 날짜에 등록된 일정 불러오기
     *
     * @param startAt, finishAt
     */
    public List<ScheduleResponseDto> read(Long userId, LocalDateTime startAt, LocalDateTime finishAt) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserExceptionCode.USER_NOT_FOUND));

        List<Schedule> mySchedules = scheduleRepository.findByUserIdAndStartAtBetweenOrderByStartAtAsc(userId, startAt, finishAt);

        // 짝꿍이 있는 경우, 짝꿍의 일정도 추가한다.
        if (user.getMate() != null) {
            List<Schedule> mateSchedules = scheduleRepository.findByUserIdAndStartAtBetweenOrderByStartAtAsc(user.getMate().getId(), startAt, finishAt);
            mySchedules.addAll(mateSchedules);
        }

        if (mySchedules.isEmpty()) {
            throw new GlobalException(ScheduleExceptionCode.SCHEDULE_NOT_FOUND);
        }
        return mySchedules.stream()
                .map(ScheduleResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 일정 수정
     * 기존의 입력사항을 유지한채 일정 항목을 모두 받아서 update
     */
    public ScheduleResponseDto update(Long userId, Long scheduleId, ScheduleRequestDto scheduleRequestDto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GlobalException(ScheduleExceptionCode.SCHEDULE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserExceptionCode.USER_NOT_FOUND));

        validate(scheduleRequestDto.startAt(), scheduleRequestDto.finishAt());

        schedule.update(
                scheduleRequestDto.title(),
                scheduleRequestDto.content(),
                scheduleRequestDto.startAt(),
                scheduleRequestDto.finishAt()
        );

        return ScheduleResponseDto.fromEntity(schedule);
    }

    /**
     * 일정 삭제 - 삭제 버튼을 누를 경우 삭제
     *
     * @param scheduleId
     */
    public void delete(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GlobalException(ScheduleExceptionCode.SCHEDULE_NOT_FOUND));

        scheduleRepository.deleteById(scheduleId);
    }

    public void validate(LocalDateTime startAt, LocalDateTime finishAt) {
        // 시작 날짜가 종료 날짜보다 앞선 경우 or 종료 날짜가 시작 날짜보다 앞선 경우
        if (startAt.isAfter(finishAt) || finishAt.isBefore(startAt)) {
            throw new GlobalException(ScheduleExceptionCode.INVALID_DATETIME);
        }
    }
}
