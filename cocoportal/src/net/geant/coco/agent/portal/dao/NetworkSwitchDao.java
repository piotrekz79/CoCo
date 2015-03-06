/**
 * 
 */
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

/**
 * @author rvdp
 *
 */
@Component
public class NetworkSwitchDao {
    private NamedParameterJdbcTemplate jdbc;
    
    @Autowired
    public void setDataSource(DataSource jdbc) {
        this.jdbc = new NamedParameterJdbcTemplate(jdbc);
    }
    
    public List<NetworkSwitch> getNetworkSwitches() {

        return jdbc.query("select * from switches", new RowMapper<NetworkSwitch>() {

            @Override
            public NetworkSwitch mapRow(ResultSet rs, int rowNum) throws SQLException {
                NetworkSwitch networkSwitch = new NetworkSwitch();

                networkSwitch.setId(rs.getInt("id"));
                networkSwitch.setName(rs.getString("name"));
                networkSwitch.setX(rs.getInt("x"));
                networkSwitch.setY(rs.getInt("y"));

                return networkSwitch;
            }

        });
    }
    
    public NetworkSwitch getNetworkSwitches(int id) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbc.queryForObject("select * from switches where id = :id",
                params, new RowMapper<NetworkSwitch>() {

                    @Override
                    public NetworkSwitch mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        NetworkSwitch networkSwitch = new NetworkSwitch();

                        networkSwitch.setId(rs.getInt("id"));
                        networkSwitch.setName(rs.getString("name"));
                        networkSwitch.setX(rs.getInt("x"));
                        networkSwitch.setY(rs.getInt("y"));

                        return networkSwitch;
                    }

                });
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
