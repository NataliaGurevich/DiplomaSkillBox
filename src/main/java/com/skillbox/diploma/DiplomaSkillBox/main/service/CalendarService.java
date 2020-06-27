package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CalendarResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class CalendarService {

    private final PostRepository postRepository;
    private Calendar calendar = Calendar.getInstance();
    private String currentYear = Integer.toString(calendar.get(Calendar.YEAR));

    @Autowired
    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public CalendarResponse postsPerDate(String year) {

        try {
            year = (year == null || Integer.parseInt(year) > Integer.parseInt(currentYear) || year.length() < 4) ?
                    currentYear : year;
        } catch (NumberFormatException ex) {
            year = currentYear;
        }

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

            Integer countPostsPerDay = postRepository.findCountPostsForCalendar(Instant.now(), dayForQuery).get();

            log.info("POST PRO DAY {} - {}", day, countPostsPerDay);

            posts.put(day, countPostsPerDay);
        }
        return new CalendarResponse(years, posts);
    }
}
