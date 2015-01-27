package nl.surfnet.coco.agent.portal;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.surfnet.coco.agent.portal.CoCoVPN;

import java.io.PrintWriter;

/**
 * Servlet implementation class ClientServlet
 */
@WebServlet("/CoCoPortal")
public class ClientServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClientServlet() {
		// super();
		System.out.println("ClientServlet instance created");
	}

	public void init(ServletConfig config) throws ServletException {
		Topology t = new Topology();

		super.init(config);
		System.out.println("ClientServlet init");
		ServletContext ctx = config.getServletContext();
		t.setId("foobar");
		ctx.setAttribute("topo", t);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter printWriter = response.getWriter();
		printWriter.println("<h1>client test Hello World!</h1>");
		// gotoPage("/test.jsp", request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Topology topo = new Topology();
		ServletContext ctx = request.getSession().getServletContext();
		Topology topo = (Topology) ctx.getAttribute("topo");

		topo.printGraph();
		//CoCoVPN vpn = new CoCoVPN();

		// do we need to clear all forwarding rules on the switches?
		String reset = request.getParameter("reset");
		System.out.println("reset is " + reset);
		if ((reset != null) && (reset.equals("reset"))) {
			topo.removeAllVpns();
		}

		// get the list of sites of the new VPN
		String sites[] = request.getParameterValues("site");
		if (sites != null) {
			//vpn.setSites(sites);

			topo.addVpn(sites);
			// ClientTest.puttest();
			//HttpSession session = request.getSession(true);
			//session.setAttribute("vpn", vpn);
		}
		
		int id = 1;
		String parm = request.getParameter("vpn");
		System.out.println("vpn parameter is " + id);

		try {
			id = Integer.parseInt(parm);
		} catch (NullPointerException e) {
			System.out.println("cannot convert to int: " + parm);
			id = 1;
		} catch (NumberFormatException e) {
			System.out.println("cannot convert to int: " + parm);
			id = 1;
		}
		topo.setActiveVpn(id);
		
		//response.setContentType("text/html");
		// PrintWriter printWriter = response.getWriter();

		// String result = "Sites: ";
		/*
		 * Set<String> sites = vpn.getSites(); for (String s: sites) { result +=
		 * String.format(" %s ", s); }
		 */
		/*
		 * for (int i = 0; i < sites.length; i++) { result +=
		 * String.format(" %s ", sites[i]); } printWriter.println(result);
		 */

		gotoPage("/coco-portal.jsp", request, response);
	}

	private void gotoPage(String address, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

}
