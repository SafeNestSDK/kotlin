## [2.4.0] - 2026-03-16

### Added
- `analyzeDocument()` method for PDF document safety analysis (`POST /api/v1/safety/document`)
- `DocumentEndpointName` enum for valid document detection endpoints
- Data classes: `AnalyzeDocumentInput`, `DocumentExtractionSummary`, `DocumentPageEndpointResult`, `DocumentPageResult`, `DocumentFlaggedPage`, `DocumentAnalysisResult`
- Convenience overload with named parameters

## [1.2.0] - 2026-02-16

### Added
- Voice streaming support via WebSocket (`voiceStream()`)
- `creditsUsed` field on all result types
