package com.openjfx.database.mysql.impl;

import com.openjfx.database.DataCharset;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.DatabaseCharsetModel;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * msql charset
 *
 * @author yangkui
 * @since 1.0
 */
public class MysqlCharset implements DataCharset {

    private static final List<DatabaseCharsetModel> CHARSETS = new ArrayList<>();

    static {
        var fs = VertexUtils.getFileSystem();
        var buffer = fs.readFileBlocking("database/charset.json");
        var array = buffer.toJsonArray();
        //order by charset name first letter
        for (Object o : array) {
            var json = (JsonObject) o;
            var charset = json.mapTo(DatabaseCharsetModel.class);
            CHARSETS.add(charset);
        }
    }

    @Override
    public String getCharset(String collation) {
        var optional = CHARSETS.stream().filter(m -> m.getCollations().contains(collation)).findAny();
        if (optional.isEmpty()) {
            return "";
        } else {
            return optional.get().getCharset();
        }
    }

    @Override
    public List<String> getCharsetCollations(String charsetName) {
        for (DatabaseCharsetModel model : CHARSETS) {
            if (model.getCharset().equals(charsetName)) {
                return model.getCollations();
            }
        }
        return List.of();
    }

    @Override
    public List<String> getCharset() {
        return CHARSETS.stream().map(DatabaseCharsetModel::getCharset).collect(Collectors.toList());
    }
}
