package org.sense.flink.pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ValenciaTraffic implements Serializable {
	private static final long serialVersionUID = -3147914413052930222L;
	private Integer id;
	private String street;
	private Date update;
	private Integer status;
	private List<Point> coordinates;
	private String uri;

	// 2019-07-22T12:51:04.681+02:00
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	public ValenciaTraffic() {
	}

	public ValenciaTraffic(Integer id, String street, String update, Integer status, String coordinates, String uri) {
		this.id = id;
		this.street = street;
		this.status = status;
		this.coordinates = Point.extract(coordinates);
		this.uri = uri;
		try {
			this.update = formatter.parse(update);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ValenciaTraffic [id=" + id + ", street=" + street + ", update=" + update + ", status=" + status
				+ ", coordinates=" + coordinates + ", uri=" + uri + "]";
	}

}