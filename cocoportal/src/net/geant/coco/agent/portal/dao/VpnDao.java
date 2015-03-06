package net.geant.coco.agent.portal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class VpnDao {
private NamedParameterJdbcTemplate jdbc;
    
    @Autowired
    public void setDataSource(DataSource jdbc) {
        this.jdbc = new NamedParameterJdbcTemplate(jdbc);
    }
    
    public List<Vpn> getVpns() {
        return jdbc.query("select * from vpns", new RowMapper<Vpn>() {
            @Override
            public Vpn mapRow(ResultSet rs, int rowNum) throws SQLException {
                Vpn vpn = new Vpn();
                
                vpn.setId(rs.getInt("id"));
                vpn.setName(rs.getString("name"));
                vpn.setMplsLabel(rs.getInt("mpls_label"));
                
                return vpn;
            }
        });
    }
}
