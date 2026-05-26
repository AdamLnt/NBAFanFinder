import axios from "axios";
import type { GeocodingResult } from "../types/map";

const NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org";

interface NominatimAddress {
  house_number?: string;
  road?: string;
  pedestrian?: string;
  city?: string;
  town?: string;
  village?: string;
  municipality?: string;
  postcode?: string;
  country?: string;
}

interface NominatimResult {
  display_name: string;
  lat: string;
  lon: string;
  address?: NominatimAddress;
}

const mapResult = (item: NominatimResult): GeocodingResult => {
  const addr = item.address ?? {};
  return {
    displayName: item.display_name,
    numero: addr.house_number ?? "",
    rue: addr.road ?? addr.pedestrian ?? "",
    ville: addr.city ?? addr.town ?? addr.village ?? addr.municipality ?? "",
    codePostal: addr.postcode ?? "",
    pays: addr.country ?? "",
    latitude: parseFloat(item.lat),
    longitude: parseFloat(item.lon),
  };
};

export const geocodingService = {
  async search(query: string): Promise<GeocodingResult[]> {
    if (!query || query.trim().length < 3) {
      return [];
    }
    try {
      const response = await axios.get<NominatimResult[]>(`${NOMINATIM_BASE_URL}/search`, {
        params: {
          q: query,
          format: "json",
          addressdetails: 1,
          limit: 5,
        },
      });
      return response.data.map(mapResult);
    } catch {
      return [];
    }
  },

  async reverse(latitude: number, longitude: number): Promise<GeocodingResult | null> {
    try {
      const response = await axios.get<NominatimResult>(`${NOMINATIM_BASE_URL}/reverse`, {
        params: {
          lat: latitude,
          lon: longitude,
          format: "json",
          addressdetails: 1,
        },
      });
      return mapResult(response.data);
    } catch {
      return null;
    }
  },
};
