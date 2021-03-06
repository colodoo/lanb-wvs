package com.colodoo.framework.creater.service;

import com.colodoo.framework.creater.config.CreaterCfg;
import com.colodoo.framework.creater.model.Column;
import com.colodoo.framework.creater.model.Tables;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface CreaterMapper {

    @Select({"SELECT\n" +
            "\tcolumn_name as columnName,\n" +
            "\tcolumn_type as columnType,\n" +
            "\tis_nullable as isNullable,\n" +
            "\tcolumn_key as columnKey,\n" +
            "\tcolumn_comment as columnComment\n" +
            "FROM\n" +
            "\tinformation_schema.COLUMNS " +
            "WHERE\n" +
            "\ttable_name = #{tableName} " +
            "and table_schema = #{tableSchema}"})
    public List<Column> getColumns(@Param("tableName") String tableName, @Param("tableSchema") String tableSchema);

    @Select("SELECT\n" +
            "\tt.TABLE_NAME AS tableName,\n" +
            "\tt.TABLE_COMMENT AS tableComment\n" +
            "FROM\n" +
            "\tinformation_schema.TABLES t \n" +
            "WHERE\n" +
            "\tt.TABLE_SCHEMA = #{tableSchema}")
    public List<Tables> getTables(@Param("tableSchema") String tableSchema);

}
