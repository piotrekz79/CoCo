package net.geant.coco.agent.portal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
        // VPN with id equal to 1 is special; it contains all free sites
        return jdbc.query("select * from vpns where id != 1",
                new RowMapper<Vpn>() {
                    @Override
                    public Vpn mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Vpn vpn = new Vpn();

                        vpn.setId(rs.getInt("id"));
                        vpn.setName(rs.getString("name"));
                        vpn.setMplsLabel(rs.getInt("mpls_label"));

                        return vpn;
                    }
                });
    }

    public boolean addSite(String vpnName, String siteName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vpn", vpnName);
        params.addValue("site", siteName);

        String query = "UPDATE site2vpn "
                + "INNER JOIN vpns ON site2vpn.vpnid = vpns.id "
                + "INNER JOIN sites ON site2vpn.siteid = sites.id, "
                + "(SELECT id FROM vpns WHERE name=:vpn) vpn  "
                + "SET site2vpn.vpnid = vpn.id  WHERE sites.name = :site ;";
        System.out.println("vpnDao addSite: " + query);
        return jdbc.update(query, params) == 1;
    }
    
    public boolean deleteSite(String siteName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("site", siteName);

        String query = "UPDATE site2vpn "
                + "INNER JOIN vpns ON site2vpn.vpnid = vpns.id "
                + "INNER JOIN sites ON site2vpn.siteid = sites.id "
                + "SET site2vpn.vpnid = 1  WHERE sites.name = :site ;";
        System.out.println("vpnDao deleteSite: " + query);
        return jdbc.update(query, params) == 1;
    }
}
