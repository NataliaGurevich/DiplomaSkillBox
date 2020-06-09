package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.TagToPostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceBySearch {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private TagToPostRepository tagToPostRepository;

    @Autowired
    private EntityManager entityManager;


    public PostsResponse getPostsBySearch(int offset, int limit, String querySearch) throws InterruptedException {

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        List<PostResponse> posts = new ArrayList<>();
        Page<Post> postPage;
        List<Post> postList;
        long count;

        if (querySearch != null && querySearch.length() > 0) {

            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();

            QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Post.class)
                    .get();

            Date now = new Date();

            Query query = queryBuilder
                    .bool()
                    .must(queryBuilder.keyword()
                            .onField("moderationStatus").matching("ACCEPTED")
                            .createQuery())
                    .must(queryBuilder.range()
                            .onField("time").below(now)
                            .createQuery())
                    .must(queryBuilder.keyword().wildcard()
                            .onFields("text", "title").matching(querySearch + "*")
                            .createQuery())
                    .createQuery();

            FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Post.class);

            Sort sort = queryBuilder.sort()
                    .byField("time")
                    .desc()
                    .createSort();

            jpaQuery.setSort(sort);
            jpaQuery.setFirstResult(offset);
            jpaQuery.setMaxResults(limit);

            postList = jpaQuery.getResultList();

            count = jpaQuery.getResultSize();

            log.info("POST SEARCH {}, totalPosts {}", postList.size(), count);

            if (postList.size() > 0) {
                posts = cretePostList(postList);
            }
        } else {
            postPage = postRepository.findAll(paging);
            count = postPage.getSize();

            postList = postPage.stream().collect(Collectors.toList());

            log.info("POST ALL {}", postList);

            if (postList != null) {
                posts = cretePostList(postList);
            }
        }
        PostsResponse postsResponse = getAllPostResponse(count, posts);
        return postsResponse;
    }

    private List<PostResponse> cretePostList(List<Post> postList) {
        List<PostResponse> posts = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);
            posts.add(PostMapper.converter(post, likeCount, disLikeCount, commentCount));
        }

        return posts;
    }

    public PostsResponse getAllPostResponse(long count, List<PostResponse> posts) {

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setCount(count);
        postsResponse.setPosts(posts);

        log.info("POSTS {}", postsResponse);

        return postsResponse;
    }
}
