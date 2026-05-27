import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { MapContainer, TileLayer, Marker, Popup, Circle, useMap } from "react-leaflet";
import L from "leaflet";
import markerIconUrl from "leaflet/dist/images/marker-icon.png";
import markerIcon2xUrl from "leaflet/dist/images/marker-icon-2x.png";
import markerShadowUrl from "leaflet/dist/images/marker-shadow.png";
import { Header } from "../components/Header";
import { mapService } from "../services/mapService";
import { teamService } from "../services/teamService";
import { authService } from "../services/authService";
import type { MapUser } from "../types/map";
import type { Team } from "../types/team";
import "../styles/MapPage.css";

// Bundle Leaflet's default marker assets via Vite so they are served same-origin
// (the CSP only allows images from 'self', data: and the tile server).
const defaultIcon = L.icon({
  iconUrl: markerIconUrl,
  iconRetinaUrl: markerIcon2xUrl,
  shadowUrl: markerShadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});
L.Marker.prototype.options.icon = defaultIcon;

const RADIUS_STEPS_KM = [5, 10, 20, 50, 100] as const;
const EARTH_RADIUS_KM = 6371;

const toRadians = (deg: number) => (deg * Math.PI) / 180;

const distanceKm = (a: [number, number], b: [number, number]): number => {
  const [lat1, lon1] = a;
  const [lat2, lon2] = b;
  const dLat = toRadians(lat2 - lat1);
  const dLon = toRadians(lon2 - lon1);
  const sinLat = Math.sin(dLat / 2);
  const sinLon = Math.sin(dLon / 2);
  const h =
    sinLat * sinLat + Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) * sinLon * sinLon;
  return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
};

interface RecenterProps {
  position: [number, number] | null;
}

const RecenterMap = ({ position }: RecenterProps) => {
  const map = useMap();
  useEffect(() => {
    if (position) {
      map.flyTo(position, 12);
    }
  }, [position, map]);
  return null;
};

export const MapPage = () => {
  const navigate = useNavigate();
  const currentUserId = authService.getUser()?.id ?? 0;

  const [users, setUsers] = useState<MapUser[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [selectedTeamId, setSelectedTeamId] = useState<number | "">("");
  const [radiusKm, setRadiusKm] = useState<number | null>(null);
  const [homePosition, setHomePosition] = useState<[number, number] | null>(null);
  const [flyTo, setFlyTo] = useState<[number, number] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    teamService
      .getAll()
      .then(setTeams)
      .catch(() => setTeams([]));
  }, []);

  useEffect(() => {
    mapService.getMyLocation().then((loc) => {
      if (loc) {
        const coords: [number, number] = [loc.latitude, loc.longitude];
        setHomePosition(coords);
        setFlyTo(coords);
      }
    });
  }, []);

  useEffect(() => {
    let ignore = false;
    // eslint-disable-next-line react-hooks/set-state-in-effect -- fetch-on-dep-change pattern: re-fetch users when selectedTeamId changes
    setLoading(true);
    mapService
      .getUsers(selectedTeamId === "" ? null : selectedTeamId)
      .then((data) => {
        if (ignore) return;
        setUsers(data);
        setError(null);
      })
      .catch((err) => {
        if (ignore) return;
        setError(err instanceof Error ? err.message : "Erreur de chargement");
      })
      .finally(() => {
        if (ignore) return;
        setLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, [selectedTeamId]);

  const filteredUsers = useMemo(() => {
    if (radiusKm === null || !homePosition) return users;
    return users.filter((u) => distanceKm(homePosition, [u.latitude, u.longitude]) <= radiusKm);
  }, [users, radiusKm, homePosition]);

  const countLabel = filteredUsers.length > 1 ? "utilisateurs" : "utilisateur";

  const handleRecenter = () => {
    if (homePosition) {
      setFlyTo(homePosition);
    }
  };

  const handleStartChat = (userId: number) => {
    navigate(`/chat?startWith=${userId}`);
  };

  return (
    <div className="map-page">
      <Header />

      <div className="map-body">
        <aside className="map-sidebar">
          <div className="map-sidebar__section">
            <label htmlFor="team-filter" className="map-sidebar__label">
              Filtrer par équipe
            </label>
            <select
              id="team-filter"
              className="map-search-input"
              value={selectedTeamId}
              onChange={(e) =>
                setSelectedTeamId(e.target.value === "" ? "" : Number(e.target.value))
              }
            >
              <option value="">Toutes les équipes</option>
              {teams.map((team) => (
                <option key={team.id} value={team.id}>
                  {team.nom}
                </option>
              ))}
            </select>
          </div>

          <div className="map-sidebar__section">
            <span className="map-sidebar__label">Distance maximale</span>
            <div className="radius-chips">
              <button
                type="button"
                className={`radius-chip ${radiusKm === null ? "radius-chip--selected" : ""}`}
                onClick={() => setRadiusKm(null)}
              >
                Toutes
              </button>
              {RADIUS_STEPS_KM.map((km) => (
                <button
                  type="button"
                  key={km}
                  className={`radius-chip ${radiusKm === km ? "radius-chip--selected" : ""}`}
                  onClick={() => setRadiusKm(km)}
                  disabled={!homePosition}
                >
                  {km} km
                </button>
              ))}
            </div>
            {!homePosition && (
              <p className="map-sidebar__hint">Adresse en cours de chargement...</p>
            )}
          </div>

          <div className="map-sidebar__section">
            <button
              type="button"
              className="map-locate-btn"
              onClick={handleRecenter}
              disabled={!homePosition}
            >
              Recentrer sur mon adresse
            </button>
          </div>

          <div className="map-sidebar__section">
            <p className="map-sidebar__count">
              {loading ? "Chargement..." : `${filteredUsers.length} ${countLabel}`}
            </p>
            {error && <p className="map-sidebar__error">{error}</p>}
          </div>
        </aside>

        <MapContainer center={[46.2, 2.2]} zoom={6} className="map-container" zoomControl={true}>
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <RecenterMap position={flyTo} />

          {radiusKm !== null && homePosition && (
            <Circle
              center={homePosition}
              radius={radiusKm * 1000}
              pathOptions={{ color: "#1b3a6b", fillColor: "#1b3a6b", fillOpacity: 0.08 }}
            />
          )}

          {filteredUsers.map((user) => (
            <Marker
              key={user.id}
              position={[user.latitude, user.longitude]}
              eventHandlers={{
                mouseover: (e) => e.target.openPopup(),
              }}
            >
              <Popup>
                <div className="map-popup">
                  <strong>
                    {user.prenom} {user.nom}
                  </strong>
                  <br />
                  {user.ville}
                  {user.equipes.length > 0 && (
                    <>
                      <br />
                      <em>Supporte : {user.equipes.map((t) => t.nom).join(", ")}</em>
                    </>
                  )}
                  {user.id !== currentUserId && (
                    <button
                      type="button"
                      className="map-popup__chat-btn"
                      onClick={() => handleStartChat(user.id)}
                    >
                      Discuter
                    </button>
                  )}
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </div>
  );
};
