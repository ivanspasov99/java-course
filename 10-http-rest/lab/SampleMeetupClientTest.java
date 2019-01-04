package bg.sofia.uni.fmi.mjt.meetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.Gson;

import bg.sofia.uni.fmi.mjt.meetup.dto.Event;
import bg.sofia.uni.fmi.mjt.meetup.dto.Venue;

@RunWith(MockitoJUnitRunner.class)
public class SampleMeetupClientTest {

	@Mock
	private HttpClient httpClientMock;

	@Mock
	private HttpResponse<String> httpResponseMock;

	private MeetupClient client;

	@Before
	public void setUp() {
		client = new MeetupClient(httpClientMock, null);
	}

	@Test
	public void testGetEventsNearby_WithEmptyList() throws Exception {
		when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
				.thenReturn(httpResponseMock);
		when(httpResponseMock.body()).thenReturn("[]");

		List<Event> actual = client.getEventsNearby();
		assertTrue(actual.isEmpty());
	}

	@Test
	public void testGetEventsWithVenueName_WithTwoEvents() throws Exception {
		when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
				.thenReturn(httpResponseMock);

		List<Event> events = List.of(new Event("Java meetup", new Venue("Office foo", "Sofia")),
				new Event("Java meetup #2", new Venue("Cool office", "Sofia")));
		String json = new Gson().toJson(events);
		when(httpResponseMock.body()).thenReturn(json);

		List<Event> actual = client.getEventsWithVenueName("Office foo");
		assertEquals(1, actual.size());
		assertEquals("Java meetup", actual.get(0).getName());
	}

	@Test
	public void testGetEvent_ReturnsEvent() throws Exception {
		when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
				.thenReturn(httpResponseMock);

		Event event = new Event("Java meetup #2", new Venue("Office foo", "Sofia"));
		String json = new Gson().toJson(event);
		when(httpResponseMock.body()).thenReturn(json);
		when(httpResponseMock.statusCode()).thenReturn(200);

		Event actual = client.getEvent("foo", "bar");
		assertEquals("Java meetup #2", actual.getName());
		assertEquals("Office foo", actual.getVenue().getName());
	}
}
