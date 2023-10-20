package com.newsline.dao;

import com.newsline.dto.CommentFromServer;
import com.newsline.models.Comment;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CommentsRepository implements CommentsDAO {
    private static final String FIND_ALL_COMMENTS_QUERY = "with recursive\n" +
            "    parent_comments as (SELECT id, news_id, user_id, text, parent_comment_id, created_at, modified_at, " +
            "deleted, 0 as level, ((row_number() over ()) * 10)::numeric ordered\n" +
            "                        from comments\n" +
            "                        where parent_comment_id is null\n" +
            "                          and news_id = ? and fully_deleted = false\n" +
            "                        ORDER BY created_at),\n" +
            "    child_comments as (SELECT id, news_id, user_id, text, parent_comment_id, created_at, modified_at, " +
            "deleted                       from comments\n" +
            "                       where parent_comment_id is not null\n" +
            "                         and news_id = ? and fully_deleted = false\n" +
            "                       ORDER BY created_at),\n" +
            "    joined_comments as (select *\n" +
            "                        from parent_comments pc\n" +
            "                        union all\n" +
            "                        select cc.id,\n" +
            "                               cc.news_id,\n" +
            "                               cc.user_id,\n" +
            "                               cc.text,\n" +
            "                               cc.parent_comment_id,\n" +
            "                               cc.created_at,\n" +
            "                               cc.modified_at,\n" +
            "cc.deleted,\n"+
            "                               level + 1,\n" +
            "                               (case\n" +
            "                                    when\n" +
            "                                        row_number() over () = 1 then\n" +
            "                                        ordered + (row_number() over ())::numeric * 0.0000001\n" +
            "                                    else\n" +
            "                                        ordered + (row_number() over ())::numeric * 10\n" +
            "                                   end) " +
            "                        from joined_comments jc\n" +
            "                                 join child_comments cc on jc.id = cc.parent_comment_id and jc.id <> cc.id),\n" +
            "comments_with_user_credentials as (select * from joined_comments join (select id, username from users) u" +
            " on u.id = joined_comments.user_id ORDER BY created_at)\n" +
            "SELECT *\n" +
            "from comments_with_user_credentials\n" +
            "ORDER BY ordered, created_at;";

    private static final String CAN_FULLY_DELETED_QUERY = "with recursive\n" +
            "    parent_comments as (SELECT *,\n" +
            "                               0 as                            level,\n" +
            "                               ((row_number() over ())::numeric * 10) ordered\n" +
            "                        from comments\n" +
            "                        where parent_comment_id is null\n" +
            "                          and news_id = ?\n" +
            "                          and fully_deleted = false\n" +
            "                        ORDER BY created_at),\n" +
            "    child_comments as (SELECT *\n" +
            "                       from comments\n" +
            "                       where parent_comment_id is not null\n" +
            "                         and news_id = ?\n" +
            "                         and fully_deleted = false\n" +
            "                       ORDER BY created_at),\n" +
            "    joined_comments as (select pc.id as comment_id,\n" +
            "                               pc.news_id,\n" +
            "                               pc.user_id,\n" +
            "                               pc.text,\n" +
            "                               pc.parent_comment_id,\n" +
            "                               pc.created_at,\n" +
            "                               pc.modified_at,\n" +
            "                               pc.deleted,\n" +
            "                               pc.level,\n" +
            "                               pc.ordered\n" +
            "                        from parent_comments pc\n" +
            "                        union all\n" +
            "                        select cc.id as comment_id,\n" +
            "                               cc.news_id,\n" +
            "                               cc.user_id,\n" +
            "                               cc.text,\n" +
            "                               cc.parent_comment_id,\n" +
            "                               cc.created_at,\n" +
            "                               cc.modified_at,\n" +
            "                               cc.deleted,\n" +
            "                               level + 1,\n" +
            "                               (case\n" +
            "                                    when\n" +
            "                                        row_number() over () = 1 then\n" +
            "                                        ordered + (row_number() over ())::numeric * 0.0000001\n" +
            "                                    else\n" +
            "                                        ordered + (row_number() over ())::numeric * 10\n" +
            "                                   end)\n" +
            "                        from joined_comments jc\n" +
            "                                 join child_comments cc\n" +
            "                                      on jc.comment_id = cc.parent_comment_id and jc.comment_id <> cc.id),\n" +
            "    comments_with_user_credentials as (select *\n" +
            "                                       from joined_comments\n" +
            "                                                join (select id, username from users) u\n" +
            "                                                     on u.id = joined_comments.user_id\n" +
            "                                       ORDER BY created_at),\n" +
            "    ordered_comments as (SELECT *\n" +
            "                         from comments_with_user_credentials\n" +
            "                         ORDER BY ordered, created_at),\n" +
            "    deleted_comment as (select ordered, parent_comment_id\n" +
            "                        from ordered_comments\n" +
            "                        where comment_id = ?),\n" +
            "    next_root_comment as (select *\n" +
            "                          from ordered_comments\n" +
            "                          where ordered > (select ordered from deleted_comment)\n" +
            "                            and parent_comment_id = (select deleted_comment.parent_comment_id from deleted_comment)\n" +
            "                          limit 1)\n" +
            "select deleted as can_fully_delete\n" +
            "from ordered_comments\n" +
            "where case\n" +
            "          when (select ordered from next_root_comment) is not null\n" +
            "              then\n" +
            "                      ordered > (select ordered from deleted_comment) and\n" +
            "                      ordered < (select ordered from next_root_comment)\n" +
            "          else\n" +
            "              ordered >= (select ordered from deleted_comment)\n" +
            "          end\n" +
            "ORDER BY ordered DESC\n" +
            "LIMIT 1;";

    private static final String TRY_FULLY_DELETE_QUERY = "with recursive\n" +
            "    parent_comments as (SELECT *,\n" +
            "                               0 as                            level,\n" +
            "                               (row_number() over () * 10)::numeric ordered\n" +
            "                        from comments\n" +
            "                        where parent_comment_id is null\n" +
            "                          and news_id = ?\n" +
            "                          and fully_deleted = false\n" +
            "                        ORDER BY created_at),\n" +
            "    child_comments as (SELECT *\n" +
            "                       from comments\n" +
            "                       where parent_comment_id is not null\n" +
            "                         and news_id = ?\n" +
            "                         and fully_deleted = false\n" +
            "                       ORDER BY created_at),\n" +
            "    joined_comments as (select pc.id as comment_id,\n" +
            "                               pc.news_id,\n" +
            "                               pc.user_id,\n" +
            "                               pc.text,\n" +
            "                               pc.parent_comment_id,\n" +
            "                               pc.created_at,\n" +
            "                               pc.modified_at,\n" +
            "                               pc.fully_deleted,\n" +
            "                               pc.deleted,\n" +
            "                               pc.id as base_root_comment,\n" +
            "                               pc.level,\n" +
            "                               pc.ordered\n" +
            "                        from parent_comments pc\n" +
            "                        union all\n" +
            "                        select cc.id as comment_id,\n" +
            "                               cc.news_id,\n" +
            "                               cc.user_id,\n" +
            "                               cc.text,\n" +
            "                               cc.parent_comment_id,\n" +
            "                               cc.created_at,\n" +
            "                               cc.modified_at,\n" +
            "                               cc.fully_deleted,\n" +
            "                               cc.deleted,\n" +
            "                               base_root_comment,\n" +
            "                               level + 1,\n" +
            "                               (case\n" +
            "                                    when\n" +
            "                                                    row_number() over () = 1 then\n" +
            "                                            ordered + (row_number() over ())::numeric * 0.0000001\n" +
            "                                    else\n" +
            "                                            ordered + (row_number() over ())::numeric * 10\n" +
            "                                   end)\n" +
            "                        from joined_comments jc\n" +
            "                                 join child_comments cc\n" +
            "                                      on jc.comment_id = cc.parent_comment_id and jc.comment_id <> cc.id),\n" +
            "    comments_with_user_credentials as (select *\n" +
            "                                       from joined_comments\n" +
            "                                                join (select id user_id, username from users) u\n" +
            "                                                     on u.user_id = joined_comments.user_id\n" +
            "                                       ORDER BY created_at),\n" +
            "    ordered_comments as (SELECT *\n" +
            "                         from comments_with_user_credentials\n" +
            "                         ORDER BY ordered, created_at),\n" +
            "    deleted_comment as (select ordered, parent_comment_id, base_root_comment\n" +
            "                        from ordered_comments\n" +
            "                        where comment_id = ?),\n" +
            "    current_branch as (select *\n" +
            "                       from ordered_comments\n" +
            "                       where ordered >= (select ordered from ordered_comments oc where oc.comment_id = (select base_root_comment from deleted_comment))\n" +
            "                         and ordered <= (select ordered from deleted_comment)\n" +
            "                       ORDER BY ordered DESC)\n" +
            "update comments\n" +
            "set fully_deleted = true, deleted = true\n" +
            "from current_branch cb\n" +
            "where comments.id = cb.comment_id and\n" +
            "        comments.deleted = true and\n" +
            "        (select count(*) from comments cm_2 where cb.comment_id = cm_2.parent_comment_id and cm_2.deleted = false and cm_2.fully_deleted = false) = 0;";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<CommentFromServer> ROW_MAPPER = (ResultSet resultSet, int rowNum) ->
            new CommentFromServer(resultSet.getObject("id", java.util.UUID.class),
                    resultSet.getObject("news_id", java.util.UUID.class),
                    resultSet.getString("username"), resultSet.getString("text"),
                    resultSet.getTimestamp("created_at"), resultSet.getInt("level"),
                    resultSet.getBoolean("deleted")
            );

    private final RowMapper<UUID> ROW_MAPPER_USER_ID = (ResultSet resultSet, int rowNum) ->
            resultSet.getObject("user_id", java.util.UUID.class);

    private final RowMapper<UUID> ROW_MAPPER_NEWS_ID = (ResultSet resultSet, int rowNum) ->
            resultSet.getObject("news_id", java.util.UUID.class);

    private final RowMapper<Boolean> ROW_MAPPER_CAN_FULLY_DELETE = (ResultSet resultSet, int rowNum) ->
            resultSet.getBoolean("can_fully_delete");

    public CommentsRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<CommentFromServer> findAllByNewsId(UUID newsId) {
        return jdbcTemplate.query(FIND_ALL_COMMENTS_QUERY, ROW_MAPPER, newsId, newsId);
    }

    @Override
    public Optional<Comment> getCommentById(UUID id){
        String query = "select * from comments where id = ?;";
        RowMapper<Comment> row_mapper = (ResultSet resultSet, int rowNum) ->
                new Comment(resultSet.getObject("id", java.util.UUID.class),
                        resultSet.getObject("news_id", java.util.UUID.class),
                        resultSet.getObject("user_id", java.util.UUID.class),
                        resultSet.getString("text"),
                        resultSet.getObject("parent_comment_id", java.util.UUID.class),
                        resultSet.getTimestamp("created_at"),
                        resultSet.getTimestamp("modified_at"),
                        resultSet.getBoolean("deleted")
                        );
        Comment comment = jdbcTemplate.queryForObject(query, row_mapper, id);
        return Optional.ofNullable(comment);
    }

    @Override
    public UUID getAuthorIdById(UUID id) {
        String query = "select user_id from comments where id = ?;";
        return jdbcTemplate.queryForObject(query, ROW_MAPPER_USER_ID, id);
    }

    @Override
    public void save(Comment comment) {
        String query = "insert into comments(news_id, user_id, text, parent_comment_id, created_at, modified_at)" +
                "values(?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(query, comment.getNewsId(), comment.getUserId(), comment.getText(), comment.getParentCommentId(),
                comment.getCreatedAt(), comment.getModifiedAt());
    }

    @Override
    public void update(Comment comment) {
        String query = "update comments set text = ?, modified_at = ?;";
        jdbcTemplate.update(query, comment.getText(), comment.getModifiedAt());
    }

    @Override
    public void delete(UUID id) {
        String queryForGetNewsId = "select news_id from comments where id = ?;";
        UUID newsId = jdbcTemplate.queryForObject(queryForGetNewsId, ROW_MAPPER_NEWS_ID, id);
        if (newsId != null) {
            String queryForMarkAsDeleted = "update comments set deleted = true where id = ?;";
            jdbcTemplate.update(queryForMarkAsDeleted, id);

            Boolean canFullyDelete = jdbcTemplate.queryForObject(CAN_FULLY_DELETED_QUERY, ROW_MAPPER_CAN_FULLY_DELETE, newsId, newsId, id);
            if (canFullyDelete != null && canFullyDelete) {
                System.out.println("fully");
                jdbcTemplate.update(TRY_FULLY_DELETE_QUERY, newsId, newsId, id);
            }
        }
    }

}
