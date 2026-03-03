import { MapContainer, TileLayer } from "react-leaflet";
import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import "../styles/MapPage.css";

export const MapPage = () => {
  const navigate = useNavigate();

  return (
    <div className="map-page">
      <Header onNavigateToLogin={() => navigate("/login")} />

      <div className="map-body">
        <aside className="map-sidebar">
          <div className="map-sidebar__search-wrapper">
            <input
              type="text"
              placeholder="Rechercher..."
              className="map-search-input"
            />
          </div>
        </aside>

        <MapContainer
          center={[46.2, 2.2]}
          zoom={6}
          className="map-container"
          zoomControl={true}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
        </MapContainer>
      </div>
    </div>
  );
};
