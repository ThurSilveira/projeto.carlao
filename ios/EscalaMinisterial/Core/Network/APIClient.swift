import Foundation

final class APIClient {
    static let shared = APIClient()

    private let session: URLSession
    private let decoder: JSONDecoder
    private let encoder: JSONEncoder

    private init() {
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = 30
        self.session = URLSession(configuration: config)

        self.decoder = JSONDecoder()
        self.decoder.keyDecodingStrategy = .useDefaultKeys

        self.encoder = JSONEncoder()
        self.encoder.keyEncodingStrategy = .useDefaultKeys
    }

    // MARK: - GET

    func get<T: Decodable>(_ urlString: String) async throws -> T {
        let request = try buildRequest(urlString: urlString, method: "GET", body: nil as Data?)
        return try await perform(request)
    }

    // MARK: - POST

    func post<Body: Encodable, Response: Decodable>(_ urlString: String, body: Body) async throws -> Response {
        let data = try encoder.encode(body)
        let request = try buildRequest(urlString: urlString, method: "POST", body: data)
        return try await perform(request)
    }

    func postEmpty(_ urlString: String) async throws {
        let request = try buildRequest(urlString: urlString, method: "POST", body: nil as Data?)
        try await performVoid(request)
    }

    func postEmptyResponse<Body: Encodable>(_ urlString: String, body: Body) async throws {
        let data = try encoder.encode(body)
        let request = try buildRequest(urlString: urlString, method: "POST", body: data)
        try await performVoid(request)
    }

    // MARK: - PUT

    func put<Body: Encodable, Response: Decodable>(_ urlString: String, body: Body) async throws -> Response {
        let data = try encoder.encode(body)
        let request = try buildRequest(urlString: urlString, method: "PUT", body: data)
        return try await perform(request)
    }

    // MARK: - DELETE

    func delete(_ urlString: String) async throws {
        let request = try buildRequest(urlString: urlString, method: "DELETE", body: nil as Data?)
        try await performVoid(request)
    }

    // MARK: - PATCH

    func patch<Body: Encodable, Response: Decodable>(_ urlString: String, body: Body) async throws -> Response {
        let data = try encoder.encode(body)
        let request = try buildRequest(urlString: urlString, method: "PATCH", body: data)
        return try await perform(request)
    }

    func patchEmpty<Response: Decodable>(_ urlString: String) async throws -> Response {
        let request = try buildRequest(urlString: urlString, method: "PATCH", body: nil as Data?)
        return try await perform(request)
    }

    // MARK: - Private helpers

    private func buildRequest(urlString: String, method: String, body: Data?) throws -> URLRequest {
        guard let url = URL(string: urlString) else {
            throw NetworkError.invalidURL
        }
        var request = URLRequest(url: url)
        request.httpMethod = method
        if let body = body {
            request.httpBody = body
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        }
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        return request
    }

    private func perform<T: Decodable>(_ request: URLRequest) async throws -> T {
        let (data, response) = try await session.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.invalidResponse
        }
        guard (200...299).contains(httpResponse.statusCode) else {
            let body = String(data: data, encoding: .utf8) ?? ""
            throw NetworkError.httpError(statusCode: httpResponse.statusCode, body: body)
        }
        do {
            return try decoder.decode(T.self, from: data)
        } catch {
            throw NetworkError.decodingError(error)
        }
    }

    private func performVoid(_ request: URLRequest) async throws {
        let (data, response) = try await session.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.invalidResponse
        }
        guard (200...299).contains(httpResponse.statusCode) else {
            let body = String(data: data, encoding: .utf8) ?? ""
            throw NetworkError.httpError(statusCode: httpResponse.statusCode, body: body)
        }
    }
}
