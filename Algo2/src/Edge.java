

public class Edge implements Comparable<Edge> {

        private Integer distance = 0;
        private City fromCity;
        private City toCity;

        public Edge(City from, City to, Integer distance) {
            this.fromCity = from;
            this.toCity = to;
            this.distance = distance;
        }
        
        public Integer getDistance(){
        	return distance;
        }
        
        public City getFromCity(){
        	return fromCity;        	
        }
        
        public City getToCity(){
        	return toCity;
        }
        
        public String getToCityName(){
        	return toCity.getName();
        }
        
        public String getFromCityName(){
        	return fromCity.getName();
        }

        @Override
        public int compareTo(Edge other) {
            if (distance < other.distance) return -1;
            else if (distance > other.distance) return 1;
            else return 0;
        }
    }
