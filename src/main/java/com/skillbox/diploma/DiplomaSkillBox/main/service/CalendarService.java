package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CalendarResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class CalendarService {

    private final PostRepository postRepository;

    @Autowired
    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public CalendarResponse postsPerDate(String year) {
        Set<Integer> years = new TreeSet<>();
        TreeMap<String, Integer> posts = new TreeMap();
        List<Date> datesWithPosts = postRepository.findListDates(Instant.now()).orElse(null);

        for (Date date : datesWithPosts) {
            log.info("DATES WITH POSTS {}", date.toString());
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            years.add(localDate.getYear());

            String day = String.valueOf(date);
            day = day.substring(0,10);
            String dayForQuery = day + "%";

            Integer countPostsPerDay = postRepository.findCountPostsForCalendar(dayForQuery).get();

            log.info("POST PRO DAY {} - {}", day, countPostsPerDay);

            posts.put(day, countPostsPerDay);
        }
        return new CalendarResponse(years, posts);
    }
}
