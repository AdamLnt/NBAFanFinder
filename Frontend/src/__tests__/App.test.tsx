import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";

// MapPage (importée par App) dépend de leaflet : on stub la lib.
vi.mock("react-leaflet", () => ({
  MapContainer: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  TileLayer: () => null,
  Marker: () => null,
  Popup: () => null,
  Circle: () => null,
  useMap: () => ({ flyTo: vi.fn() }),
}));
vi.mock("leaflet", () => ({
  default: { icon: () => ({}), Marker: { prototype: { options: {} } } },
}));

import App from "../App";

describe("App", () => {
  it("redirige la racine vers la page de connexion", async () => {
    render(<App />);
    expect(await screen.findByText("Connexion")).toBeInTheDocument();
  });
});
