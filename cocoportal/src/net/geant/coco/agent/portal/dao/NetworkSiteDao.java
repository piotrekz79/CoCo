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
public class NetworkSiteDao {
    private NamedParameterJdbcTemplate jdbc;

    @Autowired
    public void setDataSource(DataSource jdbc) {
        this.jdbc = new NamedParameterJdbcTemplate(jdbc);
    }

    public List<NetworkSite> getNetworkSites() {
        String query = "SELECT sites.*, " + "sites.name AS switch_name, "
                + "vpns.name AS vpn_name " + "FROM sites "
                + "INNER JOIN switches ON sites.switch = switches.id "
                + "INNER JOIN site2vpn ON sites.id = site2vpn.siteid "
                + "INNER JOIN vpns ON vpns.id = site2vpn.vpnid;";
        // System.out.println(query);
        return jdbc.query(query, new RowMapper<NetworkSite>() {

            @Override
            public NetworkSite mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                NetworkSite networkSite = new NetworkSite();

                networkSite.setId(rs.getInt("id"));
                networkSite.setName(rs.getString("name"));
                networkSite.setX(rs.getInt("x"));
                networkSite.setY(rs.getInt("y"));
                networkSite.setProviderSwitch(rs.getString("switch_name"));
                networkSite.setProviderPort(rs.getInt("remote_port"));
                networkSite.setCustomerPort(rs.getInt("local_port"));
                networkSite.setVlanId(rs.getInt("vlanid"));
                networkSite.setIpv4Prefix(rs.getString("ipv4prefix"));
                networkSite.setMacAddress(rs.getString("mac_address"));
                networkSite.setVpnName(rs.getString("vpn_name"));

                return networkSite;
            }

        });
    }

    public List<NetworkSite> getNetworkSites(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        String query = "SELECT sites.*, " + "sites.name AS switch_name "
                + "FROM sites " + "JOIN switches WHERE switch = switches.id "
                + "AND sites.id = :id;";
        return jdbc.query(query, params, new RowMapper<NetworkSite>() {

            @Override
            public NetworkSite mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                NetworkSite networkSite = new NetworkSite();

                networkSite.setId(rs.getInt("id"));
                networkSite.setName(rs.getString("name"));
                networkSite.setX(rs.getInt("x"));
                networkSite.setY(rs.getInt("y"));
                networkSite.setProviderSwitch(rs.getString("switch_name"));
                networkSite.setProviderPort(rs.getInt("remote_port"));
                networkSite.setCustomerPort(rs.getInt("local_port"));
                networkSite.setVlanId(rs.getInt("vlanid"));
                networkSite.setIpv4Prefix(rs.getString("ipv4prefix"));
                networkSite.setMacAddress(rs.getString("mac_address"));
                networkSite.setVpnName(rs.getString("vpn_name"));

                return networkSite;
            }

        });
    }

    public List<NetworkSite> getNetworkSites(String vpnName) {
        if (vpnName == null) {
            vpnName = "all";
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vpn", vpnName);

        String query = "SELECT sites.*, " + "sites.name AS switch_name, "
                + "vpns.name AS vpn_name " + "FROM sites "
                + "INNER JOIN switches ON switch = switches.id "
                + "INNER JOIN site2vpn ON sites.id = site2vpn.siteid "
                + "INNER JOIN vpns ON vpns.id = site2vpn.vpnid "
                + "AND vpns.name = :vpn ;";
        return jdbc.query(query, params, new RowMapper<NetworkSite>() {

            @Override
            public NetworkSite mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                NetworkSite networkSite = new NetworkSite();

                networkSite.setId(rs.getInt("id"));
                networkSite.setName(rs.getString("name"));
                networkSite.setX(rs.getInt("x"));
                networkSite.setY(rs.getInt("y"));
                networkSite.setProviderSwitch(rs.getString("switch_name"));
                networkSite.setProviderPort(rs.getInt("remote_port"));
                networkSite.setCustomerPort(rs.getInt("local_port"));
                networkSite.setVlanId(rs.getInt("vlanid"));
                networkSite.setIpv4Prefix(rs.getString("ipv4prefix"));
                networkSite.setMacAddress(rs.getString("mac_address"));
                networkSite.setVpnName(rs.getString("vpn_name"));

                return networkSite;
            }

        });
    }
}
